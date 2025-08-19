package com.docx.parsers;

import com.docx.models.ResponseDoc;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaDocParser {

    private static final Pattern PARAM_PATTERN = Pattern.compile("@param\\s+(\\w+)\\s+(.+)");
    private static final Pattern RETURN_PATTERN = Pattern.compile("@return\\s+(.+)");
    private static final Pattern THROWS_PATTERN = Pattern.compile("@throws\\s+(\\w+)\\s+(.+)");
    private static final Pattern AUTHOR_PATTERN = Pattern.compile("@author\\s+(.+)");
    private static final Pattern SINCE_PATTERN = Pattern.compile("@since\\s+(.+)");
    private static final Pattern VERSION_PATTERN = Pattern.compile("@version\\s+(.+)");
    private static final Pattern DEPRECATED_PATTERN = Pattern.compile("@deprecated\\s*(.*)");
    
    // Custom API tags
    private static final Pattern API_NOTE_PATTERN = Pattern.compile("@apiNote\\s+(.+)");
    private static final Pattern API_DESCRIPTION_PATTERN = Pattern.compile("@apiDescription\\s+(.+)");
    private static final Pattern API_RESPONSE_PATTERN = Pattern.compile("@apiResponse\\s+(\\d+)\\s+(.+)");
    private static final Pattern API_ERROR_PATTERN = Pattern.compile("@apiError\\s+(\\d+)\\s+(.+)");
    private static final Pattern API_EXAMPLE_PATTERN = Pattern.compile("@apiExample\\s+(.+)");

    public ParsedJavaDoc parseJavaDoc(String javaDocComment) {
        if (javaDocComment == null || javaDocComment.trim().isEmpty()) {
            return new ParsedJavaDoc();
        }

        // Clean up the JavaDoc comment
        String cleanedComment = cleanJavaDocComment(javaDocComment);
        
        ParsedJavaDoc result = new ParsedJavaDoc();
        
        // Extract main description (everything before the first @tag)
        String mainDescription = extractMainDescription(cleanedComment);
        result.setDescription(mainDescription);
        
        // Parse standard JavaDoc tags
        result.setParameters(parseParameters(cleanedComment));
        result.setReturnDescription(parseReturn(cleanedComment));
        result.setThrows(parseThrows(cleanedComment));
        result.setAuthor(parseAuthor(cleanedComment));
        result.setSince(parseSince(cleanedComment));
        result.setVersion(parseVersion(cleanedComment));
        result.setDeprecated(parseDeprecated(cleanedComment));
        
        // Parse custom API tags
        result.setApiNote(parseApiNote(cleanedComment));
        result.setApiDescription(parseApiDescription(cleanedComment));
        result.setApiResponses(parseApiResponses(cleanedComment));
        result.setApiErrors(parseApiErrors(cleanedComment));
        result.setApiExamples(parseApiExamples(cleanedComment));
        
        return result;
    }

    private String cleanJavaDocComment(String javaDocComment) {
        // Remove /** and */ and leading * from each line
        return javaDocComment
            .replaceAll("^/\\*\\*", "")
            .replaceAll("\\*/$", "")
            .replaceAll("(?m)^\\s*\\*\\s?", "")
            .trim();
    }

    private String extractMainDescription(String cleanedComment) {
        String[] lines = cleanedComment.split("\n");
        StringBuilder description = new StringBuilder();
        
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("@")) {
                break;
            }
            if (!description.toString().isEmpty()) {
                description.append(" ");
            }
            description.append(line);
        }
        
        return description.toString().trim();
    }

    private Map<String, String> parseParameters(String cleanedComment) {
        Map<String, String> parameters = new LinkedHashMap<>();
        Matcher matcher = PARAM_PATTERN.matcher(cleanedComment);
        
        while (matcher.find()) {
            String paramName = matcher.group(1);
            String paramDesc = matcher.group(2);
            parameters.put(paramName, paramDesc);
        }
        
        return parameters;
    }

    private String parseReturn(String cleanedComment) {
        Matcher matcher = RETURN_PATTERN.matcher(cleanedComment);
        return matcher.find() ? matcher.group(1) : null;
    }

    private Map<String, String> parseThrows(String cleanedComment) {
        Map<String, String> throwsMap = new LinkedHashMap<>();
        Matcher matcher = THROWS_PATTERN.matcher(cleanedComment);
        
        while (matcher.find()) {
            String exceptionType = matcher.group(1);
            String description = matcher.group(2);
            throwsMap.put(exceptionType, description);
        }
        
        return throwsMap;
    }

    private String parseAuthor(String cleanedComment) {
        Matcher matcher = AUTHOR_PATTERN.matcher(cleanedComment);
        return matcher.find() ? matcher.group(1) : null;
    }

    private String parseSince(String cleanedComment) {
        Matcher matcher = SINCE_PATTERN.matcher(cleanedComment);
        return matcher.find() ? matcher.group(1) : null;
    }

    private String parseVersion(String cleanedComment) {
        Matcher matcher = VERSION_PATTERN.matcher(cleanedComment);
        return matcher.find() ? matcher.group(1) : null;
    }

    private String parseDeprecated(String cleanedComment) {
        Matcher matcher = DEPRECATED_PATTERN.matcher(cleanedComment);
        return matcher.find() ? matcher.group(1) : null;
    }

    private String parseApiNote(String cleanedComment) {
        Matcher matcher = API_NOTE_PATTERN.matcher(cleanedComment);
        return matcher.find() ? matcher.group(1) : null;
    }

    private String parseApiDescription(String cleanedComment) {
        Matcher matcher = API_DESCRIPTION_PATTERN.matcher(cleanedComment);
        return matcher.find() ? matcher.group(1) : null;
    }

    private List<ResponseDoc> parseApiResponses(String cleanedComment) {
        List<ResponseDoc> responses = new ArrayList<>();
        Matcher matcher = API_RESPONSE_PATTERN.matcher(cleanedComment);
        
        while (matcher.find()) {
            int statusCode = Integer.parseInt(matcher.group(1));
            String description = matcher.group(2);
            responses.add(new ResponseDoc(statusCode, description));
        }
        
        return responses;
    }

    private List<ResponseDoc> parseApiErrors(String cleanedComment) {
        List<ResponseDoc> errors = new ArrayList<>();
        Matcher matcher = API_ERROR_PATTERN.matcher(cleanedComment);
        
        while (matcher.find()) {
            int statusCode = Integer.parseInt(matcher.group(1));
            String description = matcher.group(2);
            errors.add(new ResponseDoc(statusCode, description));
        }
        
        return errors;
    }

    private List<String> parseApiExamples(String cleanedComment) {
        List<String> examples = new ArrayList<>();
        Matcher matcher = API_EXAMPLE_PATTERN.matcher(cleanedComment);
        
        while (matcher.find()) {
            examples.add(matcher.group(1));
        }
        
        return examples;
    }

    public static class ParsedJavaDoc {
        private String description;
        private Map<String, String> parameters = new HashMap<>();
        private String returnDescription;
        private Map<String, String> throwsDescriptions = new HashMap<>();
        private String author;
        private String since;
        private String version;
        private String deprecated;
        private String apiNote;
        private String apiDescription;
        private List<ResponseDoc> apiResponses = new ArrayList<>();
        private List<ResponseDoc> apiErrors = new ArrayList<>();
        private List<String> apiExamples = new ArrayList<>();

        // Getters and setters
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Map<String, String> getParameters() { return parameters; }
        public void setParameters(Map<String, String> parameters) { this.parameters = parameters; }
        
        public String getReturnDescription() { return returnDescription; }
        public void setReturnDescription(String returnDescription) { this.returnDescription = returnDescription; }
        
        public Map<String, String> getThrows() { return throwsDescriptions; }
        public void setThrows(Map<String, String> throwsDescriptions) { this.throwsDescriptions = throwsDescriptions; }
        
        public String getAuthor() { return author; }
        public void setAuthor(String author) { this.author = author; }
        
        public String getSince() { return since; }
        public void setSince(String since) { this.since = since; }
        
        public String getVersion() { return version; }
        public void setVersion(String version) { this.version = version; }
        
        public String getDeprecated() { return deprecated; }
        public void setDeprecated(String deprecated) { this.deprecated = deprecated; }
        
        public String getApiNote() { return apiNote; }
        public void setApiNote(String apiNote) { this.apiNote = apiNote; }
        
        public String getApiDescription() { return apiDescription; }
        public void setApiDescription(String apiDescription) { this.apiDescription = apiDescription; }
        
        public List<ResponseDoc> getApiResponses() { return apiResponses; }
        public void setApiResponses(List<ResponseDoc> apiResponses) { this.apiResponses = apiResponses; }
        
        public List<ResponseDoc> getApiErrors() { return apiErrors; }
        public void setApiErrors(List<ResponseDoc> apiErrors) { this.apiErrors = apiErrors; }
        
        public List<String> getApiExamples() { return apiExamples; }
        public void setApiExamples(List<String> apiExamples) { this.apiExamples = apiExamples; }
    }
}