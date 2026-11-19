### AI Task: Exception Message Review

**Exception message before:**

"Unknown job type: PYTHON"

**AI feedback:**

The original message identifies the unsupported job type, but it does not tell the developer which job types are accepted. A teammate unfamiliar with the code would need to investigate the implementation to understand how to correct the problem.

**Improved exception message:**

"Unsupported job type 'PYTHON'. Supported job types are CMD and FT."

**What I changed and why:**

I included the unsupported value and explicitly listed the supported job types. This makes the error more actionable, allowing a developer to identify the problem and correct the input without having to inspect the source code.