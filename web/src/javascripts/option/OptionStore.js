import {observable, computed, action} from "mobx";
import Uploader from '../upload/Uploader';
import Gallery from 'react-fine-uploader/gallery';
import React, {Component} from "react";
const fetchOptions = function (store) {
    $.ajax({
        url: '/option/get',
        success: data => {
            data = JSON.parse(data);
            store.segmentSize = data.segmentSize;
            store.replicaSize = data.replicaSize;
            store.uploadAlgorithm = data.uploadAlgorithm;
            store.balanceAlgorithm = data.balanceAlgorithm;
            store.speedFactor = data.speedFactor;
            store.balancedPreference = data.balancedPreference;
            store.utilizationThreshold = data.utilizationThreshold;
            store.dhtModulo = data.dhtModulo;
            store.initUploader();
        }
    })
};
class OptionStore {
    constructor() {
        this.update = this.update.bind(this);
        this.segmentSizes = [1024 * 128, 1024 * 256, 1024 * 512, 1024 * 1024, 1024 * 1024 * 2, 1024 * 1024 * 4, 1024 * 1024 * 8];
        this.uploadAlgorithms = ['HDFS_DEFAULT', 'HDFS_BALANCED_FILE', 'HDFS_BALANCED_GLOBAL', 'HADAPS', 'DISTRIBUTED_HASH_TABLE'];
        this.balanceAlgorithms = ['HDFS_BALANCER', 'DISTRIBUTED_HASH_TABLE', 'HADAPS'];
        this.update();
        this.initUploader();
    }

    initUploader() {
        this.gallery = null;
        this.uploader = new Uploader(this);
        this.gallery = <Gallery uploader={ this.uploader }
                                ref={e => {
                                    if (e) {
                                        this.galleryRef = e
                                    }
                                }}
                                fileInput-multiple={false}
                                dropzone-multiple={false}
                                validation={{
                                    itemLimit: 1
                                }}
        />;
    }

    update() {
        fetchOptions(this);
    }

    @observable gallery;
    @observable uploader;
    @observable segmentSize;
    @observable replicaSize;
    @observable uploadAlgorithm;
    @observable balanceAlgorithm;
    @observable speedFactor;
    @observable balancedPreference;
    @observable dhtModulo;
}

export default OptionStore;