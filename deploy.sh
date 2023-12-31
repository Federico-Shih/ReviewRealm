#!/bin/bash

#if mvn clean package;
#then
#    printf "Maven build successful\n"
#else
#    printf "Maven build failed\n"
#    exit 1
#fi

if [ $# -eq 1 ]
then
	u=$1
else
	u=${USER}
fi

cp webapp/target/webapp.war webapp/target/app.war

scp webapp/target/app.war "${u}"@pampero.it.itba.edu.ar:/home/"${u}"/.

ssh -tt "${u}"@pampero.it.itba.edu.ar << EOF
	export SSHPASS=m5jx4ksYH
	sshpass -e sftp -oBatchMode=no -b - paw-2023a-04@10.16.1.110 << !
	cd web
	put app.war
	bye
!
EOF