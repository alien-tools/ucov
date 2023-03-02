package com.github.ucov.reports.html.types;

public record Usage2(int usage_id,
                     int element_id,
                     int client_id,
                     int begin_line,
                     int end_line,
                     int begin_column,
                     int end_column,
                     String usage_type,
                     String role,
                     String context) {
}
