<template>
  <el-dialog
    v-model="visible"
    title="编辑项目"
    width="min(520px, 92vw)"
    :close-on-click-modal="false"
    :close-on-press-escape="!saving"
    :before-close="handleBeforeClose"
    @closed="resetForm"
  >
    <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
      <el-form-item label="项目名称" prop="name">
        <el-input
          v-model="form.name"
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
          placeholder="选填，清空这里并保存即可删除描述"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button :disabled="saving" @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="saving" @click="submit">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { updateProject } from '../../api/project'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  // 父页面传进来的当前项目；可以是 null（详情还没拿到时不允许编辑）
  project: { type: Object, default: null }
})
const emit = defineEmits(['update:modelValue', 'updated'])

// 父页面 v-model 控制开关；子组件不能直接改 props，只能把新值抛回去
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
      /* required 拦不住全空格；maxlength 只是输入框层面的限制，
         这里再按“原始长度先判断、再 trim 判空”的口径兜一次 */
      validator: (rule, value, callback) => {
        const raw = value || ''
        if (raw.length > 100) return callback(new Error('项目名称最多 100 个字符'))
        if (!raw.trim()) return callback(new Error('项目名称不能为空或全空格'))
        callback()
      },
      trigger: 'blur'
    }
  ],
  description: [{ max: 500, message: '项目描述最多 500 个字符', trigger: 'blur' }]
}

/**
 * 每次打开都从 props.project 重新复制一份，绝不直接改 props 里的对象。
 * 只监听“打开”这个动作，不监听 project 变化：
 * 保存成功后父页面会重新拉详情并刷新 project，如果监听 project 会把请求后的结果
 * 又灌回表单，反而掩盖真实数据。
 */
watch(
  () => props.modelValue,
  isOpen => {
    if (!isOpen) return
    form.name = props.project?.name ?? ''
    form.description = props.project?.description ?? ''
    // 上一次打开可能留下红色报错，重新打开时先清掉
    formRef.value?.clearValidate()
  },
  { immediate: true }
)

async function submit() {
  // 闸一：正在保存，快速双击的第二下直接结束
  if (saving.value) return

  // 闸二：没有有效编号就不发请求（project 为 null 或 id 不是正整数）
  const id = Number(props.project?.id)
  if (!Number.isInteger(id) || id <= 0) {
    ElMessage.warning('项目还没加载完成，请返回列表重新进入')
    return
  }

  // 闸三：前端校验不通过就不发请求，用户留在表单里改
  try {
    await formRef.value.validate()
  } catch {
    return
  }

  saving.value = true
  try {
    // 请求体只组装两个字段：id 走路径，ownerId/createTime 由后端保持原值
    // 名称 trim 后提交；描述清空时传 ""，这样后端能明确把字段写成空字符串
    const payload = {
      name: form.name.trim(),
      description: form.description.trim()
    }
    const updated = await updateProject(id, payload)
    ElMessage.success('保存成功')
    // 只有服务器真的保存成功才发 updated；父页面收到后自己重新拉详情
    emit('updated', updated)
    visible.value = false
  } catch (error) {
    // 失败：提示但不关闭、不清空，用户可以直接改完再点保存
    ElMessage.error(error instanceof Error ? error.message : '保存失败，请重试')
  } finally {
    // 任何结果都要恢复，否则按钮会一直转圈
    saving.value = false
  }
}

function handleBeforeClose(done) {
  // 保存进行中不允许用右上角 X 关掉，避免请求还在飞、弹窗已经没了
  if (saving.value) return
  done()
}

function resetForm() {
  form.name = ''
  form.description = ''
  formRef.value?.clearValidate()
}
</script>
