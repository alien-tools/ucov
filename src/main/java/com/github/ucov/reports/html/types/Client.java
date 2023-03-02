package com.github.ucov.reports.html.types;

public record Client(int client_id,
                     String client_type,
                     String client_name,
                     String client_package,
                     String client_class,
                     String client_function,
                     String file_name,
                     int begin_line,
                     int end_line,
                     int begin_column,
                     int end_column) {
}
