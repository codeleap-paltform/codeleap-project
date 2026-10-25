<template>
  <el-dialog
    v-model="visible"
    title="创建项目"
    width="min(520px, 92vw)"
    :close-on-click-modal="false"
    :before-close="handleBeforeClose"
    @closed="resetForm"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
      <el-form-item label="项目名称" prop="name">
        <el-input
          v-model.trim="form.name"
          maxlength="100"
          show-word-limit
          placeholder="例如：码跃项目"
        />
      </el-form-item>
      <el-form-item label="项目描述" prop="description">
        <el-input
          v-model="form.description"
          type="textarea"
          :rows="4"
          maxlength="500"
          show-word-limit
          placeholder="选填，简单说明这个项目要做什么"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button :disabled="saving" @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="saving" @click="submit">创建</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { createProject } from '../../api/project'

const props = defineProps({
  modelValue: { type: Boolean, default: false }
})
const emit = defineEmits(['update:modelValue', 'created'])

// 父页面用 v-model 控制开关：读到 props.modelValue，关闭时把新值抛回父页面
const visible = computed({
  get: () => props.modelValue,
  set: value => emit('update:modelValue', value)
})

const formRef = ref()
const saving = ref(false)
const form = reactive({ name: '', description: '' })

const rules = {
  name: [
    { required: true, message: '请输入项目名称', trigger: 'blur' },
    {
      // required 拦不住全空格，所以再补一条 trim 之后的自定义校验
      validator: (rule, value, callback) => {
        const name = (value || '').trim()
        if (!name) return callback(new Error('项目名称不能为空或全空格'))
        if (name.length > 100) return callback(new Error('项目名称最多 100 个字符'))
        callback()
      },
      trigger: 'blur'
    }
  ],
  description: [{ max: 500, message: '项目描述最多 500 个字符', trigger: 'blur' }]
}

async function submit() {
  // 第一道闸：正在保存时，快速双击的第二下直接结束
  if (saving.value) return

  // 第二道闸：前端校验不通过就不发请求，用户还停在这个表单里
  try {
    await formRef.value.validate()
  } catch {
    return
  }

  saving.value = true
  try {
    const description = form.description.trim()
    // 请求体只有两个字段，ownerId / createTime 由后端决定
    await createProject({ name: form.name.trim(), description: description || null })
    ElMessage.success('创建成功')
    emit('created')
    visible.value = false
  } catch (error) {
    // 失败：提示但不清空、不关闭，保留用户输入以便修改重试
    ElMessage.error(error instanceof Error ? error.message : '创建失败，请重试')
  } finally {
    // 无论成功失败都必须恢复，否则按钮会一直转圈
    saving.value = false
  }
}

function handleBeforeClose(done) {
  // 请求进行中不允许用右上角 X 关掉，避免关完请求又成功造成误解
  if (saving.value) return
  done()
}

function resetForm() {
  form.name = ''
  form.description = ''
  formRef.value?.clearValidate()
}
</script>
