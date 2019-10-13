package com.sitaluo.breakpointDownload.core;
class PartResult {

        public PartResult(int number, long start, long end) {
            this.number = number;
            this.start = start;
            this.end = end;
        }
        
        public long getStart() {
            return start;
        }

        public void setStart(long start) {
            this.start = start;
        }

        public long getEnd() {
            return end;
        }

        public void setEnd(long end) {
            this.end = end;
        }

        public int getNumber() {
            return number;
        }

        public boolean isFailed() {
            return failed;
        }

        public void setFailed(boolean failed) {
            this.failed = failed;
        }

        public Exception getException() {
            return exception;
        }

        public void setException(Exception exception) {
            this.exception = exception;
        }

        private int number; // part number, starting from 1.
        private long start; // start index in the part.
        private long end;  // end index in the part.
        private boolean failed; // flag of part upload failure.
        private Exception exception; // Exception during part upload.
    }