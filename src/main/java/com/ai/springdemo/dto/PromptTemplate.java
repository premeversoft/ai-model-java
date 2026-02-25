package com.ai.springdemo.dto;

import java.util.List;

public class PromptTemplate {
    private String id;
    private String name;
    private String description;
    private String template;
    private List<TemplateVariable> variables;

    public PromptTemplate() {
    }

    public PromptTemplate(String id, String name, String description, String template, List<TemplateVariable> variables) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.template = template;
        this.variables = variables;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public List<TemplateVariable> getVariables() {
        return variables;
    }

    public void setVariables(List<TemplateVariable> variables) {
        this.variables = variables;
    }

    public static class TemplateVariable {
        private String name;
        private String label;
        private String placeholder;
        private boolean required;

        public TemplateVariable() {
        }

        public TemplateVariable(String name, String label, String placeholder, boolean required) {
            this.name = name;
            this.label = label;
            this.placeholder = placeholder;
            this.required = required;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getPlaceholder() {
            return placeholder;
        }

        public void setPlaceholder(String placeholder) {
            this.placeholder = placeholder;
        }

        public boolean isRequired() {
            return required;
        }

        public void setRequired(boolean required) {
            this.required = required;
        }
    }
}
