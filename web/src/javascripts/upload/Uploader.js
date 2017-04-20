import FineUploaderTraditional from 'fine-uploader-wrappers';

export default class Uploader {
  constructor() {
    return new FineUploaderTraditional({
      options: {
        chunking: {
          enabled: true,
          success: {endpoint: '/uploadSuccess'},
          concurrent: {
            enabled: true
          },
          partSize: 1024*1024
        },
        request: {
          endpoint: '/upload'
        },
        retry: {
          enableAuto: true
        }
      }
    })
  }
}