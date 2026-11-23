
import json
from collections import Counter
from pathlib import Path


def main():
    # Load the JSON export from the same folder as this script
    file_path = Path(__file__).with_name("customer_export.json")

    with file_path.open("r", encoding="utf-8") as file:
        data = json.load(file)

    jobs = data.get("jobs", [])

    print("========== CUSTOMER EXPORT PROFILE ==========")
    print(f"Total jobs: {len(jobs)}")

    # 1. Jobs per type
    print("\n===== JOBS PER TYPE =====")
    type_counts = Counter(job.get("type", "<MISSING>") for job in jobs)
    for job_type, count in sorted(type_counts.items()):
        print(f"{job_type}: {count}")

    # 2. Jobs per chain
    print("\n===== JOBS PER CHAIN =====")
    chain_counts = Counter(
        job.get("chain") or "<MISSING>" for job in jobs
    )
    for chain, count in sorted(chain_counts.items()):
        print(f"{chain}: {count}")

    # 3. Jobs per machine
    print("\n===== JOBS PER MACHINE =====")
    machine_counts = Counter(
        job.get("machine") or "<MISSING>" for job in jobs
    )
    for machine, count in sorted(machine_counts.items()):
        print(f"{machine}: {count}")

    # 4. Jobs with no owner
    print("\n===== JOBS WITH NO OWNER =====")
    no_owner = [
        job for job in jobs
        if not str(job.get("owner") or "").strip()
    ]
    for job in no_owner:
        print(job.get("name"))
    print(f"Total jobs without owner: {len(no_owner)}")

    # 5. Duplicate job names
    print("\n===== DUPLICATE JOB NAMES =====")
    name_counts = Counter(job.get("name") for job in jobs)
    duplicates = {
        name: count
        for name, count in name_counts.items()
        if name is not None and count > 1
    }
    for name, count in sorted(duplicates.items()):
        print(f"{name}: {count} occurrences")
    print(f"Total duplicate names: {len(duplicates)}")

    # 6. Command jobs with no command
    print("\n===== COMMAND JOBS WITH NO COMMAND =====")
    missing_commands = [
        job for job in jobs
        if job.get("type") == "CMD"
        and not str(job.get("command") or "").strip()
    ]
    for job in missing_commands:
        print(job.get("name"))
    print(f"Total command jobs without command: {len(missing_commands)}")

    # 7. File transfer jobs with no target
    print("\n===== FILE TRANSFER JOBS WITH NO TARGET =====")
    missing_targets = [
        job for job in jobs
        if job.get("type") == "FT"
        and not str(job.get("target") or "").strip()
    ]
    for job in missing_targets:
        print(job.get("name"))
    print(f"Total file transfer jobs without target: {len(missing_targets)}")


if __name__ == "__main__":
    main()
