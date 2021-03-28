package socialnetwork.domain.validators;

import socialnetwork.domain.Message;

import java.util.Arrays;
import java.util.List;

public class MessageValidator implements Validator<Message> {
    @Override
    public void validate(List<String> data) throws ValidationException {
        final String[] errors = {""};
        if (data.get(1).equals("") || data.get(2).equals("")) {
            errors[0] = errors[0] + "The IDs cannot be empty!\n";
        }

        if (data.get(5).equals("")) {
            errors[0] = errors[0] + "The reply id cannot be empty!\n";
        }

        if (!data.get(1).matches("[0-9]+") || !data.get(5).matches("-?[0-9]+")) {
            errors[0] = errors[0] + "The ids cannot contain letters or symbols!\n";
        } else {
            List<String> to = Arrays.asList(data.get(2).split(" "));
            to.forEach(s -> {
                if (!s.matches("[0-9]+")) {
                    errors[0] = errors[0] + "The ids cannot contain letters or symbols!\n";
                    throw new ValidationException(errors[0]);
                }
            });
        }

        if (!errors[0].equals("")) {
            throw new ValidationException(errors[0]);
        }
    }

    public void validateID(String id) throws ValidationException {
        if (id.equals("")) {
            throw new ValidationException("The ID cannot be empty!\n");
        } else if (!id.matches("[0-9]+")) {
            throw new ValidationException("The ID cannot contain letters or symbols!\n");
        }
    }
}