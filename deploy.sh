#!/bin/bash

scp ./webapp/target/webapp.war fshih@pampero.itba.edu.ar:/home/fshih/web/app.war
ssh -t fshih@pampero.itba.edu.ar "cd /home/fshih/web/;"
