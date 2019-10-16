package com.sitaluo.breakpointDownload;

import com.sitaluo.breakpointDownload.core.event.ProgressEvent;
import com.sitaluo.breakpointDownload.core.event.ProgressEventType;
import com.sitaluo.breakpointDownload.core.event.ProgressListener;

/** 
 * @ClassName: SimpleDemoProgressListener
 * @description: 
 * @author: sitaluo
 * @Date: 2019年10月16日
 */
public class SimpleDemoProgressListener implements ProgressListener {
    private long bytesRead = 0;
    private long totalBytes = -1;
    private boolean succeed = false;
    @Override
    public void progressChanged(ProgressEvent progressEvent) {
        long bytes = progressEvent.getBytes();
        ProgressEventType eventType = progressEvent.getEventType();
        switch (eventType) {
        case TRANSFER_STARTED_EVENT:
            System.out.println("开始下载......");
            break;
        case RESPONSE_CONTENT_LENGTH_EVENT:
            this.totalBytes = bytes;
            System.out.println("总共"+this.totalBytes + " 字节需要被下载");
            break;
        case RESPONSE_BYTE_TRANSFER_EVENT:
            this.bytesRead += bytes;
            if (this.totalBytes != -1) {
                int percent = (int)(this.bytesRead * 100.0 / this.totalBytes);
                System.out.println("下载了"+bytes + "字节，下载进度: "+ percent+"%(" + this.bytesRead + "/" + this.totalBytes + ")");
            } else {
                System.out.println("下载了"+ bytes + "字节，下载进度未知:%" + "(" + this.bytesRead + "/...)");
            }
            break;
        case TRANSFER_COMPLETED_EVENT:
            this.succeed = true;
            System.out.println("下载成功, 总共" + this.bytesRead + "字节");
            break;
        case TRANSFER_FAILED_EVENT:
            System.out.println("下载失败, 已经下载了" + this.bytesRead + "字节");
            break;
        default:
            break;
        }
    }
    public boolean isSucceed() {
        return succeed;
    }
}
 