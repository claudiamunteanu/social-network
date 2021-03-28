package socialnetwork.domain.validators;

import socialnetwork.domain.User;
import sun.security.validator.ValidatorException;

import java.util.List;

public class UserValidator implements Validator<User> {
    @Override
    public void validate(List<String> data) throws ValidationException {
        String errors = "";
        if (data.get(0).equals("")) {
            errors = errors + "The first name cannot be empty!\n";
        } else if (!data.get(0).matches("[A-Za-z]+")) {
            errors = errors + "The first name cannot contain numbers or symbols!\n";
        }

        if (data.get(1).equals("")) {
            errors = errors + "The last name cannot be empty!\n";
        } else if (!data.get(1).matches("[A-Za-z]+")) {
            errors = errors + "The last name cannot contain numbers or symbols!\n";
        }

        if (data.get(2).equals("")) {
            errors = errors + "The username cannot be empty!\n";
        }

        if(data.get(3).equals("")){
            errors = errors + "The password cannot be empty!\n";
        }

        if (!errors.equals("")) {
            throw new ValidationException(errors);
        }
    }

    public void validateName(String firstName, String lastName) throws ValidationException {
        String errors = "";
        if (firstName.equals("")) {
            errors = errors + "The first name cannot be empty!\n";
        } else if (!firstName.matches("[A-Za-z]+")) {
            errors = errors + "The first name cannot contain numbers or symbols!\n";
        }

        if (lastName.equals("")) {
            errors = errors + "The last name cannot be empty!\n";
        } else if (!lastName.matches("[A-Za-z]+")) {
            errors = errors + "The last name cannot contain numbers or symbols!\n";
        }

        if (!errors.equals("")) {
            throw new ValidationException(errors);
        }
    }
}
