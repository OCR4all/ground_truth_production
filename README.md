# Ground Truth Production Web

[![Build Status](https://travis-ci.org/OCR4all/ground_truth_production.svg?branch=master)](https://travis-ci.org/OCR4all/ground_truth_production)

Java server application for the correction of text lines.

## Table of Contents
- [Installation](#installation)
  * [Linux](#linux)
  
### Installation

#### Linux
This guide uses tomcat8 and Ubuntu (please adjust accordingly for your setup)

* Install required packages:
	`apt-get install tomcat8 maven openjdk-8-jdk`
* Setup tomcat 8 accordingly:
 	_Additional information about this step read [here](https://tomcat.apache.org/tomcat-8.0-doc/setup.html)_
* Clone Repository:
	`git clone https://github.com/OCR4all/ground_truth_production.git`
* Compile:
	`mvn clean install -f ground_truth_production/pom.xml
* Copy or link the created war file to tomcat:
	* Copy `cp ground_truth_production/target/GTC_Web.war /var/lib/tomcat8/webapps/GTC_Web.war`
* Start Tomcat:
	`systemctl start tomcat8`
	* (Restart Tomcat via `systemctl restart tomcat8`)
	* (To start Tomcat automatically at system boot `systemctl enable tomcat8`)
 
      
