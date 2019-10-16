package com.sitaluo.breakpointDownload;

import com.sitaluo.breakpointDownload.core.DownloadClient;
import com.sitaluo.breakpointDownload.core.DownloadFileRequest;
import com.sitaluo.breakpointDownload.core.DownloadFileResult;
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
/**
 * 参考了阿里云oss的sdk,阿里云sdk的断点续传下载是针对oss上的文件，修改成了可以下载任意网络文件
 * 
 * @ClassName: DownloadFacade
 * @description: 对外提供下载功能门面类
 * @author: sitaluo
 * @Date: 2019年10月13日
 */
public class DownloadFacade {
	
	/**
	 * 断点续传多线程分片下载
	 * @param downloadFileRequest
	 * @return
	 * @throws Throwable
	 */
	public static DownloadFileResult breakpointDownload(DownloadFileRequest downloadFileRequest) throws Throwable {
		return DownloadClient.breakpointDownload(downloadFileRequest);
	}
}
