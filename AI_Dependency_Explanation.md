AI Task — Failure Dependency Explanation
1. Selected example
Dependent job: KL_INV_NOTIFY_06
Upstream job: KL_INV_RECONCILE_05
Dependency condition in the source XML: failure

The source XML defines the dependency as:
<dependsOn condition="failure">KL_INV_RECONCILE_05</dependsOn>
2. Explanation in plain language
KL_INV_NOTIFY_06 should run only when KL_INV_RECONCILE_05 finishes with a failure status.

In practical terms:
- If KL_INV_RECONCILE_05 fails, the dependency condition is satisfied and KL_INV_NOTIFY_06 becomes eligible to run.
- If KL_INV_RECONCILE_05 completes successfully, the failure condition is not satisfied, so KL_INV_NOTIFY_06 should not run on the basis of this dependency.
This is a failure-handling or notification step: it is intended to run when the reconciliation job fails.
3. Expected JSON representation
The converted platform represents the dependency through a preconditions entry. The expected representation of the source failure condition is Error:
{
  "name": "KL_INV_NOTIFY_06",
  "definitionType": "OS",
  "runAs": "finsvc",
  "server": "klx-app01",
  "description": "Notify step 6 for KL_INVENTORY_SYNC",
  "script": "/opt/kl/inv/notify_06.sh",
  "parameters": [],
  "preconditions": [
    {
      "waitFor": "KL_INV_RECONCILE_05",
      "onStatus": "Error"
    }
  ]
}
4. Comparison with the supplied converted JSON
The supplied converted_export.json currently contains:
"preconditions": [
  {
    "waitFor": "KL_INV_RECONCILE_05",
    "onStatus": "Completed"
  }
]
This does not preserve the source condition. Completed represents successful completion, whereas the source explicitly specifies failure.
5. Mapping verification
Source XML	Expected JSON	Meaning
condition="success"	"onStatus": "Completed"	Run when the upstream job completes successfully
condition="failure"	"onStatus": "Error"	Run when the upstream job fails


For KL_INV_NOTIFY_06, the correct mapping is therefore:

failure → Error
6. Finding
Result: Dependency condition mismatch.

The source XML requires KL_INV_NOTIFY_06 to run when KL_INV_RECONCILE_05 fails. The supplied converted JSON instead specifies Completed, which changes the execution behavior.

The converted JSON should be corrected to use:
"onStatus": "Error"
7. Conclusion
The dependency condition is operationally significant. Mapping failure to Completed changes when the notification job is triggered and does not preserve the source workflow logic. The mapping table and converted output should therefore use Error for this dependency.