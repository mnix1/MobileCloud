import FineUploaderTraditional from 'fine-uploader-wrappers';

export default class Uploader {
    constructor(store) {
        const options = {
            options: {
                chunking: {
                    enabled: true,
                    success: {endpoint: '/file/uploadSuccess'},
                    concurrent: {
                        enabled: store.uploadAlgorithm === 'HDFS_DEFAULT'
                    },
                    partSize: store.segmentSize || 1024 * 1024
                },
                request: {
                    endpoint: '/file/upload'
                },
                retry: {
                    enableAuto: false
                }
            }
        };
        console.log(options);
        return new FineUploaderTraditional(options)
    }
}