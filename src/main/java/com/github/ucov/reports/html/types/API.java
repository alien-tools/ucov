package com.github.ucov.reports.html.types;

public record API(int element_id,
                  String package_name,
                  String class_name,
                  String name,
                  String file_path,
                  String file_name,
                  int begin_line,
                  int end_line,
                  int begin_column,
                  int end_column,
                  String symbol_type,
                  String visibility,
                  String modifier,
                  String compatibilityTest,
                  String compatibilityClient,
                  String compatibilityExample) {
}
