
# AI Review – Job Class Hierarchy

## 1. Shared fields in Job
- name
- chain
- owner
- machine
- description
- dependencies

These fields are shared by both CMD and FT jobs.

## 2. CMD-specific field
- command

CommandJob also validates that a command has been provided.

## 3. FT-specific fields
- source
- target

FileTransferJob validates that both source and target have been provided.

## 4. Hierarchy review

The class hierarchy separates shared job properties from
type-specific properties.

The abstract Job class provides common fields, getters,
and shared description behaviour.

CommandJob and FileTransferJob extend Job and implement
their own type identification and validation methods.

## 5. AI suggestion rejected

Suggestion: Move the shared printDescription() method
into both subclasses.

Reason for rejection: Both job types need to display
the same common information. Keeping this method in
the parent class avoids duplicated code and makes
maintenance easier.

## 6. Conclusion

The current hierarchy is appropriate because it uses
inheritance to share common behaviour while allowing
each subclass to handle its own specific requirements.
