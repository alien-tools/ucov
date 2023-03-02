package com.github.ucov.reports.html.types;

public record Compatibility(int compatibility_id,
                            String package_name,
                            String file_name,
                            int nb_field_tested,
                            int nb_field_halfTested,
                            int nb_field_noTested,
                            int nb_field_total,
                            int nb_method_tested,
                            int nb_method_halfTested,
                            int nb_method_noTested,
                            int nb_method_total,
                            int nb_type_tested,
                            int nb_type_halfTested,
                            int nb_type_noTested,
                            int nb_type_total,
                            int nb_constructor_tested,
                            int nb_constructor_halfTested,
                            int nb_constructor_noTested,
                            int nb_constructor_total) {
}
