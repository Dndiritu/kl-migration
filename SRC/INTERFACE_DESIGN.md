# AI Task: Abstract Class vs Interface

I used an abstract class for `Job` because `CommandJob` and `FileTransferJob` share common properties such as name, owner, machine and dependencies.

I used an interface called `Checkable` because both jobs and schedules need to support validation, even though they represent different types of objects.

This allows me to use inheritance for shared job functionality and an interface to validate different objects through one common loop.