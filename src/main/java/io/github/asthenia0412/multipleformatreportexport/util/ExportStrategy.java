package io.github.asthenia0412.multipleformatreportexport.util;

import io.github.asthenia0412.multipleformatreportexport.entity.CodeAnalysis;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ExportStrategy {

    CompletableFuture<byte[]> export(List<CodeAnalysis> dataList);


   String getFormatName();


   String getMimeType();


   String getFileExtension();
}
