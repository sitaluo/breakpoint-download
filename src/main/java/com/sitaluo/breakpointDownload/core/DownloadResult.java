package com.sitaluo.breakpointDownload.core;

import java.util.List;

class DownloadResult {

        public List<PartResult> getPartResults() {
            return partResults;
        }

        public void setPartResults(List<PartResult> partResults) {
            this.partResults = partResults;
        }


        private List<PartResult> partResults;
    }