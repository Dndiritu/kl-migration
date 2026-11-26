import xml.etree.ElementTree as ET

tree = ET.parse("customer_export_fixed.xml")
root = tree.getroot()

for job in root.findall(".//job"):
    if job.get("name") == "KL_ARC_BACKUP_10":
        parameter = job.find("parameter[@name='RETENTION_DAYS']")

        if parameter is not None and parameter.get("value") == "30":
            print("PASS: KL_ARC_BACKUP_10 has RETENTION_DAYS=30")
        else:
            print("FAIL: Retention parameter missing or incorrect")
            