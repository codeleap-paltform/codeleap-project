<template>
  <el-dialog
    v-model="visible"
    title="添加成员"
    width="min(460px, 92vw)"
    :close-on-click-modal="false"
    :close-on-press-escape="!saving"
    :before-close="handleBeforeClose"
    @closed="resetForm"
  >
    <el-form
      ref="formRef"
      :model="form"
      :rules="rules"
      label-position="top"
      @submit.prevent="submit"
    >
      <el-form-item label="用户名" prop="username">
        <el-input
          v-model="form.username"
          maxlength="50"
          show-word-limit
          placeholder="对方注册时用的用户名"
          @keyup.enter="submit"
        />
      </el-form-item>
      <p class="member-hint">
        对方需要先在码跃注册。加入后他能查看这个项目，但不能修改内容，也不能再添加其他人。
      </p>
    </el-form>

    <template #footer>
      <el-button :disabled="saving" @click="visible = false">取消</el-button>
      <el-button type="primary" :loading="saving" @click="submit">添加</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { addProjectMember } from '../../api/project'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  // 父页面传当前项目的编号；还没加载完时可能是 null
  projectId: { type: [Number, String], default: null }
})
const emit = defineEmits(['update:modelValue', 'added'])

// 开关由父页面的 v-model 决定，子组件只能把新值抛回去，不能直接改 props
const visible = computed({
  get: () => props.modelValue,
  set: value => emit('update:modelValue', value)
})

const formRef = ref()
const saving = ref(false)
const form = reactive({ username: '' })
// 请求序号：每发起一次添加、每关一次弹窗、每换一次项目都加一，
// 回来时对不上就说明这次结果已经过期，直接丢弃
let activeSeq = 0

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    {
      // required 拦不住全空格，这里按“原始长度先判断、再 trim 判空”兜一次
      validator: (rule, value, callback) => {
        const raw = value || ''
        if (raw.length > 50) return callback(new Error('用户名最多 50 个字符'))
        if (!raw.trim()) return callback(new Error('用户名不能为空或全空格'))
        callback()
      },
      trigger: 'blur'
    }
  ]
}

// 每次打开都从空白开始，不留上一次的输入和红字
watch(
  () => props.modelValue,
  isOpen => {
    if (isOpen) {
      form.username = ''
      formRef.value?.clearValidate()
    } else {
      // 弹窗被关掉（不管是用户取消还是父页面收回），在途请求作废
      activeSeq++
    }
  },
  { immediate: true }
)

// 父页面换了项目却复用了这个组件时，上一条请求也不能再算数
watch(
  () => props.projectId,
  () => {
    activeSeq++
  }
)

function normalizeProjectId() {
  const id = Number(props.projectId)
  return Number.isInteger(id) && id > 0 ? id : null
}

async function submit() {
  // 闸一：正在提交，连点的第二下直接结束
  if (saving.value) return

  // 闸二：没有有效项目编号就不发请求
  const projectId = normalizeProjectId()
  if (!projectId) {
    ElMessage.warning('项目还没加载完成，请返回列表重新进入')
    return
  }

  // 闸三：校验不通过留在表单里改，不打扰后端
  try {
    await formRef.value.validate()
  } catch {
    return
  }

  // 提交时的编号先存下来，后面就算父页面换了项目也不影响这次请求的地址
  const username = form.username.trim()
  const seq = ++activeSeq
  saving.value = true

  try {
    // 请求体只组装 username：项目编号在路径里，操作者身份由后端从 Session 取
    await addProjectMember(projectId, { username })

    // 结果回来时已经不是这次提交了（弹窗被关、项目被换），不发通知、不提示、不关弹窗
    if (seq !== activeSeq) return

    // 重复添加也是成功，所以只说“操作成功”，不宣称新建了一条关系
    ElMessage.success('操作成功')
    emit('added')
    visible.value = false
  } catch (error) {
    if (seq !== activeSeq) return
    // 失败：提示但不关闭、不清空，用户改完就能重试
    ElMessage.error(error instanceof Error ? error.message : '添加失败，请重试')
  } finally {
    saving.value = false
  }
}

function handleBeforeClose(done) {
  // 提交还没回来时，右上角 X 点不动，避免请求在飞、弹窗先没了
  if (saving.value) return
  done()
}

function resetForm() {
  form.username = ''
  formRef.value?.clearValidate()
}
</script>

<style scoped>
.member-hint {
  margin: 0;
  color: #9ca3af;
  font-size: 12px;
  line-height: 1.7;
}
</style>
