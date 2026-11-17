
# AI Risk Assessment: Public Setters

## Question
What could go wrong if every field of Job had a
public setter in a program that fixes thousands of jobs?

## Identified Risk

Allowing every field to have a public setter could
cause accidental changes to important job information,
such as job names, types and dependencies.

This could break relationships between jobs, resulting
in incorrect execution sequences and migration failures.

## Recommendation

Keep fields that define a job's identity immutable.
Only provide setters for fields that genuinely need
to change, such as owner and machine.
