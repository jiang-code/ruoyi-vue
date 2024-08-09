<template>
  <div class="app-container">

    <el-card class="box-card">
      <h3>新增信息公告</h3>
      <el-form ref="article" :rules="rules" v-model="article" label-width="150px">
        <el-form-item label="类型" prop="type">
          <el-radio-group v-model="article.type">
            <el-radio :label="'1'">公告</el-radio>
            <el-radio :label="'0'">通知</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="article.title"/>
        </el-form-item>

        <el-form-item label="内容">
          <editor :init="editorInit" v-model="article.content"/>
        </el-form-item>
      </el-form>
    </el-card>

    <div class="op-container">
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="handlePublish">发布</el-button>
    </div>

  </div>
</template>

<style>
  .el-card {
    margin-bottom: 10px;
  }

  .el-tag + .el-tag {
    margin-left: 10px;
  }

  .input-new-keyword {
    width: 90px;
    margin-left: 10px;
    vertical-align: bottom;
  }

  .avatar-uploader .el-upload {
    width: 145px;
    height: 145px;
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
  }

  .avatar-uploader .el-upload:hover {
    border-color: #20a0ff;
  }

  .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 120px;
    height: 120px;
    line-height: 120px;
    text-align: center;
  }

  .avatar {
    width: 145px;
    height: 145px;
    display: block;
  }
</style>

<script>
import { publishArticle } from '@/api/business/article'
import { createStorage, uploadPath } from '@/api/business/storage'
import { MessageBox } from 'element-ui'
import { getToken } from '@/utils/auth'

import tinymce from 'tinymce/tinymce' //tinymce默认hidden，不引入不显示
import Editor from '@tinymce/tinymce-vue'
import 'tinymce/themes/silver/theme' // 主题文件
import 'tinymce/plugins/lists' // 列表插件
import 'tinymce/plugins/advlist' // 有序列表插件
import 'tinymce/plugins/autolink' // 当用户输入有效的完整URL时，autolink插件会自动创建超链接。
import 'tinymce/plugins/link' // 链接
import 'tinymce/plugins/print'   //打印
import 'tinymce/plugins/hr'  // 水平线
import 'tinymce/plugins/autosave' // 定时自动将编辑内容保存到浏览器本地存储中（Local Storage）
import 'tinymce/plugins/autoresize' // 可拉伸宽度
import 'tinymce/plugins/charmap' // 特殊字符
import 'tinymce/plugins/code' // 查看源码
import 'tinymce/plugins/codesample' // 插入代码
import 'tinymce/plugins/directionality' // 文字方向
import 'tinymce/plugins/emoticons' // 表情符号
import 'tinymce/plugins/emoticons/js/emojis'
import 'tinymce/plugins/fullscreen' //全屏
import 'tinymce/plugins/image' // 插入上传图片插件
import 'tinymce/plugins/importcss' //引入自己定义的css文件
import 'tinymce/plugins/insertdatetime' //插入时间日期
import 'tinymce/plugins/nonbreaking' // 空格
import 'tinymce/plugins/pagebreak' //分页
import 'tinymce/plugins/preview' // 预览
import 'tinymce/plugins/quickbars' // 快速工具栏
import 'tinymce/plugins/save' // 保存
import 'tinymce/plugins/searchreplace' //查询替换
import 'tinymce/plugins/table' // 插入表格插件
import 'tinymce/plugins/wordcount' // 字数统计插件
import 'tinymce/plugins/visualblocks' // 块范围显示

export default {
  name: 'ArticleCreate',
  components: { Editor },

  data() {
    return {
      uploadPath,
      article: { type: '1' },
      rules: {
        title: [{ required: true, message: '标题不能为空', trigger: 'blur' }],
        content: [{ required: true, message: '信息内容不能为空', trigger: 'blur' }]
      },
      editorInit: {
        height: 400,
        language_url: '/tinymce/langs/zh_CN.js',
        language: 'zh_CN',
        branding: false, // 关闭底部官网提示 默认true
        paste_data_images: true, // 允许粘贴图像
        skin_url: '/tinymce/skins/ui/oxide',  // 主题路径
        content_css: `/tinymce/skins/content/default/content.css`, // 富文本编辑器内容区域样式
        plugins: ['advlist anchor autolink autosave code codesample colorpicker colorpicker contextmenu directionality emoticons fullscreen hr image imagetools importcss insertdatetime link lists media nonbreaking noneditable pagebreak paste preview print save searchreplace spellchecker tabfocus table template textcolor textpattern visualblocks visualchars wordcount'],
        toolbar: ['searchreplace bold italic underline strikethrough alignleft aligncenter alignright outdent indent  blockquote undo redo removeformat subscript superscript code codesample', 'hr bullist numlist link image charmap preview anchor pagebreak insertdatetime media table emoticons forecolor backcolor fullscreen'],
        images_upload_handler: function(blobInfo, success, failure) {
          const formData = new FormData()
          formData.append('file', blobInfo.blob())
          createStorage(formData).then(res => {
            success(res.data.data.url)
          }).catch(() => {
            failure('上传失败，请重新上传')
          })
        }
      }
    }
  },
  computed: {
    headers() {
      return {
        'X-Dts-Admin-Token': getToken()
      }
    }
  },
  created() {
    this.init()
  },

  methods: {
    init: function() {
    },
    handleCancel: function() {
      this.$router.push({ path: '/promotion/articleList' })
    },
    handlePublish: function() {
      publishArticle(this.article).then(response => {
        this.$notify.success({
          title: '成功',
          message: '创建成功'
        })
        this.$router.push({ path: '/promotion/articleList' })
      }).catch(response => {
        MessageBox.alert('业务错误：' + response.data.errmsg, '警告', {
          confirmButtonText: '确定',
          type: 'error'
        })
      })
    }
  }
}
</script>
