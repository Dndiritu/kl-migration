
# Job Class Design Decisions

## Immutable Fields

The job name, type and chain are immutable because they
define the identity and classification of a job and should
not change accidentally after creation.

## Changeable Fields

The owner and machine are changeable because operational
responsibilities and execution environments may change.
Therefore, setters are provided for these fields only.
