<template>
  <div>
    <el-card class="ofdbox-uploader">
      <h3 style="text-align: center">OFDBox Viewer</h3>
      <el-form ref="form" :model="form" label-width="80px">
        <el-form-item label="类型">
          <el-radio-group v-model="form.isFile">
            <el-radio :label="true">文件</el-radio>
            <el-radio :label="false">URL</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="form.isFile" label="文件上传">
          <span v-if="form.file">
            {{ form.file.name }}
            <el-button
              type="danger"
              icon="el-icon-delete"
              circle
              @click="clearFile"
            ></el-button>
          </span>
          <el-upload
            v-show="!form.file"
            ref="upload"
            class="upload-demo"
            :action="baseUrl + '/upload'"
            :on-change="onChange"
            :on-success="onSuccess"
            :on-error="onError"
            :auto-upload="false"
          >
            <el-button size="small" type="primary">点击上传</el-button>
            <div slot="tip" class="el-upload__tip">
              只能上传jpg/png文件，且不超过500kb
            </div>
          </el-upload>
        </el-form-item>
        <el-form-item v-if="!form.isFile" label="URL">
          <el-input v-model="form.url"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSubmit">立即创建</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>

import axios from 'axios'
import { Message } from 'element-ui';
export default {
  data () {
    return {
      form: {
        isFile: true,
        file: null,
        url: ''
      }
    }
  },
  computed: {
    baseUrl () {
      return process.env.VUE_APP_BASE_URL;
    }
  },
  watch: {
    "form.isFile": function () {
      if (this.form.isFile) {
        this.form.file = null
        this.$refs.upload.clearFiles()
      }
    },
  },
  methods: {
    onChange (file) {
      if (file.name.indexOf('.ofd') < 0) {
        this.form.file = null
        Message.error('不是一个OFD文件');
      } else {
        this.form.file = file
      }
      this.$refs.upload.clearFiles()
    },
    onSuccess (response) {
      console.log(response)
    },
    onError (err) {
      console.log(err)
    },
    clearFile () {
      this.form.file = null

    },
    onSubmit () {
      this.upload()
    },
    upload () {
      var formData = new FormData();
      var file = this.form.file;
      formData.append("file", file.raw);
      console.log(file)

      axios.post(this.baseUrl + '/upload', formData, { 'Content-Type': 'multipart/form-data' })
        .then((res) => {
          if (res.status === 200) {
            res = res.data
            if (res.code === 200) {
              this.$router.push({
                path: '/viewer',
                query: {
                  id: res.data
                }
              })
            } else {
              Message.error(res.msg);
            }
          }
        })
        .catch(function (response) {
          console.log(response);
        });
    }
  }
}
</script>

<style scoped>
.ofdbox-uploader {
  position: absolute;
  top: 50vh;
  left: 50vw;
  width: 500px;
  transform: translate(-50%, -50%);
  -ms-transform: translate(-50%, -50%);
}
</style>