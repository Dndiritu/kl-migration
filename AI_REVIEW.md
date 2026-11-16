
# AI Review - Job Class

## Suggestions Accepted

1. Use final for the five attributes because their values
   are assigned during construction and should not be reassigned.

## Suggestions Deferred

1. Add getter methods to expose private attributes.
   These are not currently required by the assignment.

## Suggestion Rejected

1. Making attributes public.

Reason:
This would weaken encapsulation. Keeping attributes
private protects the internal state of the Job object.

## Conclusion

The Job class follows standard Java naming conventions,
uses encapsulation, and has a constructor and methods
with clearly defined responsibilities.
