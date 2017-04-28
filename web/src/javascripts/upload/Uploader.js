import FineUploaderTraditional from 'fine-uploader-wrappers';

export default class Uploader {
  constructor() {
    return new FineUploaderTraditional({
      options: {
        chunking: {
          enabled: true,
          success: {endpoint: '/file/uploadSuccess'},
          concurrent: {
            enabled: true
          },
          partSize: 1024*1024
        },
        request: {
          endpoint: '/file/upload'
        },
        retry: {
          enableAuto: true
        }
      }
    })
  }
}