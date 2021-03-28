package socialnetwork.domain.validators;

import java.util.List;

public interface Validator<T> {
    void validate(List<String> data) throws ValidationException;
}