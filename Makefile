apt_java_packages = openjdk-17-jdk
apt_python_packages = python3 python3-pip build-essential libssl-dev libffi-dev python3-dev

# Install Everything
install: install-java install-python install-maven install-rabbitmq install-gazebo install-python-venv
	# Install Python Virtual Environment
	python -m pip install venv

# Install Java
install-java:
	sudo apt install -y ${apt_java_packages}

# Install Python3
install-python:
	sudo apt install -y ${apt_python_packages}
	python -m pip install venv

# Install Maven
install-maven:
	wget https://www-us.apache.org/dist/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz -P /tmp
	sudo tar xf /tmp/apache-maven-*.tar.gz -C /opt
	sudo ln -s /opt/apache-maven-3.6.3 /opt/maven
	
	/etc/profile.d/maven.sh << EOF
	export JAVA_HOME=/usr/lib/jvm/default-java
	export M2_HOME=/opt/maven
	export MAVEN_HOME=/opt/maven
	export PATH=${M2_HOME}/bin:${PATH}
	EOF

	sudo chmod +x /etc/profile.d/maven.sh
	source /etc/profile.d/maven.sh
	rm -r /tmp

# Install RabbitMQ
install-rabbitmq:
	sudo apt-get install curl gnupg apt-transport-https -y
	curl -1sLf "https://keys.openpgp.org/vks/v1/by-fingerprint/0A9AF2115F4687BD29803A206B73A36E6026DFCA" | sudo gpg --dearmor | sudo tee /usr/share/keyrings/com.rabbitmq.team.gpg > /dev/null
	curl -1sLf https://dl.cloudsmith.io/public/rabbitmq/rabbitmq-erlang/gpg.E495BB49CC4BBE5B.key | sudo gpg --dearmor | sudo tee /usr/share/keyrings/io.cloudsmith.rabbitmq.E495BB49CC4BBE5B.gpg > /dev/null
	curl -1sLf https://dl.cloudsmith.io/public/rabbitmq/rabbitmq-server/gpg.9F4587F226208342.key | sudo gpg --dearmor | sudo tee /usr/share/keyrings/io.cloudsmith.rabbitmq.9F4587F226208342.gpg > /dev/null
	
	sudo tee /etc/apt/sources.list.d/rabbitmq.list << EOF
	deb [signed-by=/usr/share/keyrings/io.cloudsmith.rabbitmq.E495BB49CC4BBE5B.gpg] https://dl.cloudsmith.io/public/rabbitmq/rabbitmq-erlang/deb/ubuntu bionic main
	deb-src [signed-by=/usr/share/keyrings/io.cloudsmith.rabbitmq.E495BB49CC4BBE5B.gpg] https://dl.cloudsmith.io/public/rabbitmq/rabbitmq-erlang/deb/ubuntu bionic main
	deb [signed-by=/usr/share/keyrings/io.cloudsmith.rabbitmq.9F4587F226208342.gpg] https://dl.cloudsmith.io/public/rabbitmq/rabbitmq-server/deb/ubuntu bionic main
	deb-src [signed-by=/usr/share/keyrings/io.cloudsmith.rabbitmq.9F4587F226208342.gpg] https://dl.cloudsmith.io/public/rabbitmq/rabbitmq-server/deb/ubuntu bionic main
	EOF

	sudo apt-get update -y
	sudo apt-get install -y erlang-base \
                        erlang-asn1 erlang-crypto erlang-eldap erlang-ftp erlang-inets \
                        erlang-mnesia erlang-os-mon erlang-parsetools erlang-public-key \
                        erlang-runtime-tools erlang-snmp erlang-ssl \
                        erlang-syntax-tools erlang-tftp erlang-tools erlang-xmerl
	
	sudo apt-get install rabbitmq-server -y --fix-missing

# Install Gazebo
install-gazebo:
	curl -sSL http://get.gazebosim.org | sh

# Install modules to current Python Virtual Environment
install-python-modules:
	python -m pip install pika
	python -m pip install protobuf