#!/bin/bash
#
## BEGIN CONFIG ##
HOST=$(uname -n)
OPENKM_REPO="/mnt/data/openkm-repo"
OPENKM_DB="/mnt/data/openkm-db"
BACKUP_DIR_FOLDER="/mnt/backup"
BACKUP_DIR="file://$BACKUP_DIR_FOLDER"
OPENKM_POD_CONFIG="/home/david/openkm/openkm.yaml"
## END CONFIG ##

# Check root user 
if [ $(id -u) != 0 ]; then echo "You should run this script as root"; exit; fi
 
echo -e "### BEGIN: $(date +"%x %X") ###\n" 
 
# Mount disk
if mount | grep "$BACKUP_DIR_FOLDER type" > /dev/null; then
  echo "$BACKUP_DIR_FOLDER already mounted";
else
  mount "$BACKUP_DIR_FOLDER";
  
  if mount | grep "$BACKUP_DIR_FOLDER type" > /dev/null; then
    echo "$BACKUP_DIR_FOLDER mounted";
  else
    echo "$BACKUP_DIR_FOLDER error mounting";
    exit -1;
  fi
fi 

# Stop OpenKM
kubectl delete -f $OPENKM_POD_CONFIG
 
# Backup and purge old backups, Repo
duplicity remove-older-than 1M --force $BACKUP_DIR/$HOST/openkm-repo
 
if [ $(date +%u) -eq 7 ]; then
  echo "*** Full Backup Repo***"
  duplicity full --no-encryption $OPENKM_REPO $BACKUP_DIR/$HOST/openkm-repo
  RETVAL=$?
else
  echo "*** Incremental Backup Repo***"
  duplicity --no-encryption $OPENKM_REPO $BACKUP_DIR/$HOST/openkm-repo
  RETVAL=$?
fi
 
[ $RETVAL -eq 0 ] && echo "*** REPO SUCCESS ***"
[ $RETVAL -ne 0 ] && echo "*** REPO FAILURE ***"
 
# Backup and purge old backups, DB
duplicity remove-older-than 1M --force $BACKUP_DIR/$HOST/openkm-db
 
if [ $(date +%u) -eq 7 ]; then
  echo "*** Full Backup Repo***"
  duplicity full --no-encryption $OPENKM_DB $BACKUP_DIR/$HOST/openkm-db
  RETVAL=$?
else
  echo "*** Incremental Backup Repo***"
  duplicity --no-encryption $OPENKM_DB $BACKUP_DIR/$HOST/openkm-db
  RETVAL=$?
fi
 
[ $RETVAL -eq 0 ] && echo "*** DB SUCCESS ***"
[ $RETVAL -ne 0 ] && echo "*** DB FAILURE ***"
  
 
# Start OpenKM
kubectl create -f $OPENKM_POD_CONFIG 
 
echo -e "\n### END: $(date +"%x %X") ###"
 
# Status
echo "=================================";
duplicity collection-status $BACKUP_DIR/$HOST/openkm-repo
duplicity collection-status $BACKUP_DIR/$HOST/openkm-db
echo "*********************************";
df -h | grep "$BACKUP_DIR"
echo "=================================";
 
# Umount disk
sync
umount "$BACKUP_DIR_FOLDER"