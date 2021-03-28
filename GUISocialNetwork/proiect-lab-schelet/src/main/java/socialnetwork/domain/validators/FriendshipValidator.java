package socialnetwork.domain.validators;

import socialnetwork.domain.Friendship;

import java.util.List;

public class FriendshipValidator implements Validator<Friendship> {
    @Override
    public void validate(List<String> data) throws ValidationException {
        String errors = "";

        if (data.size() > 1) {
            if (data.get(1).equals("") || data.get(0).equals("")) {
                errors = errors + "The IDs cannot be empty!\n";
            } else if (!data.get(1).matches("[0-9]+") || !data.get(0).matches("[0-9]+")) {
                errors = errors + "The IDs cannot contain letters or symbols!\n";
            }
        }

        if (!errors.equals("")) {
            throw new ValidationException(errors);
        }
    }
}
