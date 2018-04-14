<!--
Copyright (c) 2017, 2017 IBM Corp. and others

This program and the accompanying materials are made available under
the terms of the Eclipse Public License 2.0 which accompanies this
distribution and is available at https://www.eclipse.org/legal/epl-2.0/
or the Apache License, Version 2.0 which accompanies this distribution and
is available at https://www.apache.org/licenses/LICENSE-2.0.

This Source Code may also be made available under the following
Secondary Licenses when the conditions for such availability set
forth in the Eclipse Public License, v. 2.0 are satisfied: GNU
General Public License, version 2 with the GNU Classpath
Exception [1] and GNU General Public License, version 2 with the
OpenJDK Assembly Exception [2].

[1] https://www.gnu.org/software/classpath/license.html
[2] http://openjdk.java.net/legal/assembly-exception.html

SPDX-License-Identifier: EPL-2.0 OR Apache-2.0 OR GPL-2.0 WITH Classpath-exception-2.0 OR LicenseRef-GPL-2.0 WITH Assembly-exception
-->

Building OpenJDK Version 9 with OpenJ9
======================================

Our website describes a simple [build process](http://www.eclipse.org/openj9/oj9_build.html)
that uses Docker and Dockerfiles to create a build environment that contains everything
you need to easily build a Linux binary of OpenJDK V9 with the Eclipse OpenJ9 virtual machine.
A more complete set of build instructions are included here for multiple platforms:

- [Linux :penguin:](#linux)
- [AIX :blue_book:](#aix)
- [Windows :ledger:](#windows)
- [MacOS :apple:](#macos)
- [ARM :iphone:](#arm)


----------------------------------

## Linux
:penguin:
This build process provides detailed instructions for building a Linux x86-64 binary of OpenJDK V9 with OpenJ9 on Ubuntu 16.04. The binary can be built directly on your system, in a virtual
machine, or in a Docker container :whale:.

If you are using a different Linux distribution, you might have to review the list of libraries that are bundled with your distribution and/or modify the instructions to use equivalent commands to the Advanced Packaging Tool (APT). For example, for Centos, substitute the `apt-get` command with `yum`.

If you want to build a binary for Linux on a different architecture, such as Power Systems&trade; or z Systems&trade;, the process is very similar and any additional information for those architectures are included as Notes :pencil: as we go along.


### 1. Prepare your system
:penguin:
Instructions are provided for preparing your system with and without the use of Docker technology.

Skip to [Setting up your build environment without Docker](#setting-up-your-build-environment-without-docker).

#### Setting up your build environment with Docker :whale:
If you want to build a binary by using a Docker container, follow these steps to prepare your system:

1. The first thing you need to do is install Docker. You can download the free Community edition from [here](https://docs.docker.com/engine/installation/), which also contains instructions for installing Docker on your system.  You should also read the [Getting started](https://docs.docker.com/get-started/) guide to familiarise yourself with the basic Docker concepts and terminology.

2. Obtain the [Linux on 64-bit x86 systems Dockerfile](https://github.com/eclipse/openj9/blob/master/buildenv/docker/jdk9/x86_64/ubuntu16/Dockerfile) to build and run a container that has all the correct software pre-requisites.

    :pencil: Dockerfiles are also available for the following Linux architectures: [Linux on 64-bit Power systems&trade;](https://github.com/eclipse/openj9/blob/master/buildenv/docker/jdk9/ppc64le/ubuntu16/Dockerfile) and [Linux on 64-bit z Systems&trade;](https://github.com/eclipse/openj9/blob/master/buildenv/docker/jdk9/s390x/ubuntu16/Dockerfile)

    Either download one of these Dockerfiles to your local system or copy and paste one of the following commands:

  - For Linux on 64-bit x86 systems, run:
```
wget https://raw.githubusercontent.com/eclipse/openj9/master/buildenv/docker/jdk9/x86_64/ubuntu16/Dockerfile
```

  - For Linux on 64-bit Power systems, run:
```
wget https://raw.githubusercontent.com/eclipse/openj9/master/buildenv/docker/jdk9/ppc64le/ubuntu16/Dockerfile
```

  - For Linux on 64-bit z Systems, run:
```
wget https://raw.githubusercontent.com/eclipse/openj9/master/buildenv/docker/jdk9/s390x/ubuntu16/Dockerfile
```

3. Next, run the following command to build a Docker image, called **openj9**:
```
docker build -t openj9 -f Dockerfile .
```

4. Start a Docker container from the **openj9** image with the following command, where `-v` maps any directory, `<host_directory>`,
on your local system to the containers `/root/hostdir` directory so that you can store the binaries, once they are built:
```
docker run -v <host_directory>:/root/hostdir -it openj9
```

:pencil: Depending on your [Docker system configuration](https://docs.docker.com/engine/reference/commandline/cli/#description), you might need to prefix the `docker` commands with `sudo`.

Now that you have the Docker image running, you are ready to move to the next step, [Get the source](#2-get-the-source).

#### Setting up your build environment without Docker

If you don't want to user Docker, you can still build an OpenJDK V9 with OpenJ9 directly on your Ubuntu system or in a Ubuntu virtual machine. Use the
[Linux on x86 Dockerfile](https://github.com/eclipse/openj9/blob/master/buildenv/docker/jdk9/x86_64/ubuntu16/Dockerfile) like a recipe card to determine the software dependencies
that must be installed on the system, plus a few configuration steps.

:pencil:
Not on x86? We also have Dockerfiles for the following Linux architectures: [Linux on Power systems](https://github.com/eclipse/openj9/blob/master/buildenv/docker/jdk9/ppc64le/ubuntu16/Dockerfile) and [Linux on z Systems](https://github.com/eclipse/openj9/blob/master/buildenv/docker/jdk9/s390x/ubuntu16/Dockerfile).


1. Install the list of dependencies that can be obtained with the `apt-get` command from the following section of the Dockerfile:
```
apt-get update \
  && apt-get install -qq -y --no-install-recommends \
    autoconf \
    ca-certificates \
    ...
    ...
```

:pencil: For Linux on z Systems, we specify the [IBM SDK for Java 8](https://developer.ibm.com/javasdk/downloads/sdk8/) in the Dockerfile rather than the `openjdk-8-jdk` package because the IBM version contains a JIT compiler that will significantly accelerate compile time.

2. This build uses the same gcc and g++ compiler levels as OpenJDK, which might be
backlevel compared with the versions you use on your system. Create links for
the compilers with the following commands:
```
ln -s g++ /usr/bin/c++
ln -s g++-4.8 /usr/bin/g++
ln -s gcc /usr/bin/cc
ln -s gcc-4.8 /usr/bin/gcc
```

3. Download and setup **freemarker.jar** into a directory. The example commands use `/root` to be consistent with the Docker instructions. If you aren't
using Docker, you probably want to store the **freemarker.jar** in your home directory.
```
cd /root
wget https://sourceforge.net/projects/freemarker/files/freemarker/2.3.8/freemarker-2.3.8.tar.gz/download -O freemarker.tgz
tar -xzf freemarker.tgz freemarker-2.3.8/lib/freemarker.jar --strip=2
rm -f freemarker.tgz
```

### 2. Get the source
:penguin:
First you need to clone the Extensions for OpenJDK for OpenJ9 project. This repository is a git mirror of OpenJDK without the HotSpot JVM, but with an **openj9** branch that contains a few necessary patches. Run the following command:
```
git clone https://github.com/ibmruntimes/openj9-openjdk-jdk9
```
Cloning this repository can take a while because OpenJDK is a large project! When the process is complete, change directory into the cloned repository:
```
cd openj9-openjdk-jdk9
```
Now fetch additional sources from the Eclipse OpenJ9 project and its clone of Eclipse OMR:
```
bash ./get_source.sh
```

### 3. Configure
:penguin:
When you have all the source files that you need, run the configure script, which detects how to build in the current build environment.
```
bash ./configure --with-freemarker-jar=/root/freemarker.jar
```
:warning: You must give an absolute path to freemarker.jar

### 4. Build
:penguin:
Now you're ready to build OpenJDK V9 with OpenJ9:
```
make all
```
:warning: If you just type `make`, rather than `make all` your build will fail, because the default `make` target is `exploded-image`. If you want to specify `make` instead of `make all`, you must add `--default-make-target=images` when you run the configure script. For more information, read this [issue](https://github.com/ibmruntimes/openj9-openjdk-jdk9/issues/34).

Two Java builds are produced: a full developer kit (jdk) and a runtime environment (jre)
- **build/linux-x86_64-normal-server-release/images/jdk**
- **build/linux-x86_64-normal-server-release/images/jre**

    :whale: If you built your binaries in a Docker container, copy the binaries to the containers **/root/hostdir** directory so that you can access them on your local system. You'll find them in the directory you set for `<host_directory>` when you started your Docker container. See [Setting up your build environment with Docker](#setting-up-your-build-environment-with-docker).

    :pencil: On other architectures the **/jdk** and **/jre** directories are in **build/linux-ppc64le-normal-server-release/images** (Linux on 64-bit Power systems) and **build/linux-s390x-normal-server-release/images** (Linux on 64-bit z Systems)

### 5. Test
:penguin:
For a simple test, try running the `java -version` command.
Change to the /jre directory:
```
cd build/linux-x86_64-normal-server-release/images/jre
```
Run:
```
./bin/java -version
```

Here is some sample output:

```
openjdk version "9-internal"
OpenJDK Runtime Environment (build 9-internal+0-adhoc.openj9-openjdk-jdk9)
Eclipse OpenJ9 VM (build 2.9, JRE 9 Linux amd64-64 Compressed References 20171030_000000 (JIT enabled, AOT enabled)
OpenJ9   - 731f323
OMR      - 7c3d3d7
OpenJDK  - 1983043 based on jdk-9+181)
```
:penguin: *Congratulations!* :tada:

----------------------------------

## AIX
:blue_book:

:construction:
This section is still under construction. Further contributions expected.

The following instructions guide you through the process of building an OpenJDK V9 binary that contains Eclipse OpenJ9 on AIX 7.2.

### 1. Prepare your system
:blue_book:
You must install the following AIX Licensed Program Products (LPPs):

- [Java8_64](https://adoptopenjdk.net/releases.html?variant=openjdk8#ppc64_aix)
- [xlc/C++ 13.1.3](https://www.ibm.com/developerworks/downloads/r/xlcplusaix/)
- x11.adt.ext

A number of RPM packages are also required. The easiest method for installing these packages is to use `yum`, because `yum` takes care of any additional dependent packages for you.

Download the following file: [yum_install_aix-ppc64.txt](aix/jdk9/yum_install_aix-ppc64.txt)

This file contains a list of required RPM packages that you can install by specifying the following command:
```
yum shell yum_install_aix-ppc64.txt
```

It is important to take the list of package dependencies from this file because it is kept right up to date by our developers.

Download and setup freemarker.jar into your home directory by running the following commands:

```
cd /<my_home_dir>
wget https://sourceforge.net/projects/freemarker/files/freemarker/2.3.8/freemarker-2.3.8.tar.gz/download -O freemarker.tgz \
tar -xzf freemarker.tgz freemarker-2.3.8/lib/freemarker.jar --strip=2 \
rm -f freemarker.tgz
```

### 2. Get the source
:blue_book:
First you need to clone the Extensions for OpenJDK for OpenJ9 project. This repository is a git mirror of OpenJDK without the HotSpot JVM, but with an **openj9** branch that contains a few necessary patches. Run the following command:
```
git clone https://github.com/ibmruntimes/openj9-openjdk-jdk9
```
Cloning this repository can take a while because OpenJDK is a large project! When the process is complete, change directory into the cloned repository:
```
cd openj9-openjdk-jdk9
```
Now fetch additional sources from the Eclipse OpenJ9 project and its clone of Eclipse OMR:

```
bash ./get_source.sh
```
### 3. Configure
:blue_book:
When you have all the source files that you need, run the configure script, which detects how to build in the current build environment.
```
bash ./configure --with-freemarker-jar=/<my_home_dir>/freemarker.jar /
                 --with-cups-include=<cups_include_path> /
                 --disable-warnings-as-errors
```
where `<my_home_dir>` is the location where you stored **freemarker.jar** and `<cups_include_path>` is the absolute path to CUPS. For example `/opt/freeware/include`.

### 4. build
:blue_book:
Now you're ready to build OpenJDK with OpenJ9:
```
make all
```
:warning: If you just type `make`, rather than `make all` your build will fail, because the default `make` target is `exploded-image`. If you want to specify `make` instead of `make all`, you must add `--default-make-target=images` when you run the configure script. For more information, read this [issue](https://github.com/ibmruntimes/openj9-openjdk-jdk9/issues/34).

Two Java builds are produced: a full developer kit (jdk) and a runtime environment (jre)
- **build/aix-ppc64-normal-server-release/images/jdk**
- **build/aix-ppc64-normal-server-release/images/jre**

    :pencil: A JRE binary is not currently generated due to an OpenJDK bug.


### 5. Test
:blue_book:
For a simple test, try running the `java -version` command.
Change to the /jdk directory:
```
cd build/aix-ppc64-normal-server-release/images/jdk
```
Run:
```
./bin/java -version
```

Here is some sample output:

```
openjdk 9-internal
OpenJDK Runtime Environment (build 9-internal+0-adhoc..openj9-openjdk-jdk9)
Eclipse OpenJ9 VM (build 2.9, JRE 9 AIX ppc64-64 Compressed References 20171017_000000 (JIT enabled, AOT enabled)
OpenJ9   - d2e7c02
OMR      - 3271be8
OpenJDK  - 437530c based on jdk-9+181)
```
:blue_book: *Congratulations!* :tada:

----------------------------------

## Windows
:ledger:

The following instructions guide you through the process of building a Windows OpenJDK V9 binary that contains Eclipse OpenJ9. This process can be used to build binaries for Windows 7, 8, and 10.

### 1. Prepare your system
:ledger:
You must install a number of software dependencies to create a suitable build environment on your system:

- [Cygwin](https://cygwin.com/install.html), which provides a Unix-style command line interface. Install all packages in the `Devel` category. In the `Archive` category, install the packages `zip` and `unzip`. Install any further package dependencies that are identified by the installer. More information about using Cygwin can be found [here](https://cygwin.com/docs.html).
- [Windows JDK 8](https://adoptopenjdk.net/releases.html#x64_win), which is used as the boot JDK.
- [Microsoft Visual Studio 2013]( https://go.microsoft.com/fwlink/?LinkId=532495), which is the same compiler level used by OpenJDK. Later levels of this compiler are not supported.
- [Freemarker V2.3.8](https://sourceforge.net/projects/freemarker/files/freemarker/2.3.8/freemarker-2.3.8.tar.gz/download)
- [Freetype2 V2.3](https://www.freetype.org/download.html)


   You can download Visual Studio, Freemarker, and Freetype manually or obtain them using the [wget](http://www.gnu.org/software/wget/faq.html#download) utility. If you choose to use `wget`, follow these steps:

- Open a cygwin terminal and change to the `/temp` directory:
```
cd /cygdrive/c/temp
```

- Run the following commands:
```
wget https://go.microsoft.com/fwlink/?LinkId=532495 -O vs2013.exe
wget https://sourceforge.net/projects/freemarker/files/freemarker/2.3.8/freemarker-2.3.8.tar.gz/download -O freemarker.tgz
wget http://download.savannah.gnu.org/releases/freetype/freetype-2.5.3.tar.gz
```
- Before installing Visual Studio, change the permissions on the installation file by running `chmod u+x vs2013.exe`.
- Install Visual Studio by running the file `vs2013.exe`.

- To unpack the Freemarker and Freetype compressed files, run:
```
tar -xzf freemarker.tgz freemarker-2.3.8/lib/freemarker.jar --strip=2
tar --one-top-level=/cygdrive/c/temp/freetype --strip-components=1 -xzf freetype-2.5.3.tar.gz
```

### 2. Get the source
:ledger:
First you need to clone the Extensions for OpenJDK for OpenJ9 project. This repository is a git mirror of OpenJDK without the HotSpot JVM, but with an **openj9** branch that contains a few necessary patches.

Run the following command in the Cygwin terminal:
```
git clone https://github.com/ibmruntimes/openj9-openjdk-jdk9
```
Cloning this repository can take a while because OpenJDK is a large project! When the process is complete, change directory into the cloned repository:
```
cd openj9-openjdk-jdk9
```
Now fetch additional sources from the Eclipse OpenJ9 project and its clone of Eclipse OMR:

```
bash ./get_source.sh
```
### 3. Configure
:ledger:
When you have all the source files that you need, run the configure script, which detects how to build in the current build environment.
```
bash configure --disable-warnings-as-errors --with-toolchain-version=2013 --with-freemarker-jar=/cygdrive/c/temp/freemarker.jar --with-freetype-src=/cygdrive/c/temp/freetype
```

:pencil: Modify the paths for freemarker and freetype if you manually downloaded and unpacked these dependencies into different directories. If Java 8 is not available on the path, add the `--with-boot-jdk=<path_to_jdk8>` configuration option.

### 4. build
:ledger:
Now you're ready to build OpenJDK with OpenJ9:
```
make all
```

Two Java builds are produced: a full developer kit (jdk) and a runtime environment (jre)
- **build/windows-x86_64-normal-server-release/images/jdk**
- **build/windows-x86_64-normal-server-release/images/jre**

### 5. Test
:ledger:
For a simple test, try running the `java -version` command.
Change to the /jdk directory:
```
cd build/windows-x86_64-normal-server-release/images/jdk
```
Run:
```
./bin/java -version
```

Here is some sample output:

```
openjdk version "9-internal"
OpenJDK Runtime Environment (build 9-internal+0-adhoc.Administrator.openj9-openjdk-jdk9)
Eclipse OpenJ9 VM (build 2.9, JRE 9 Windows 8.1 amd64-64 Compressed References 20171031_000000 (JIT enabled, AOT enabled)
OpenJ9   - 68d6fdb
OMR      - 7c3d3d72
OpenJDK  - 198304337b based on jdk-9+181)
```

:ledger: *Congratulations!* :tada:

----------------------------------

## MacOS
:apple:

:construction:
We haven't created a full build process for macOS yet? Watch this space!

----------------------------------

## ARM
:iphone:

:construction:
### Status

!!! THIS IS WORK IN PROGRESS. BEST TO WAIT UNTIL THIS MESSAGE HAS GONE !!!

OpenJDK Version 9 with OpenJ9 on ARM is at an early stage.

It builds and can run some applications with work-arounds, but there are certainly
more bugs to be found and performance has plenty of room for improvement. It is not
yet ready for production use. At this point the build is mostly useful for developers
wanting to contribute new code and fixes, but everyone is welcome to try it out.

If you find problems check for known issues on github
( https://github.com/eclipse/openj9/issues?utf8=%E2%9C%93&q=is%3Aissue+is%3Aopen+arm )
and if it looks like the problem isn't already there, please open a new issue!

### Platforms

The build is setup to cross compile in a Docker container. Cross compiling a JVM which
itself contains a compiler (JIT) has resulted in somewhat confusing terminology:

  - The *build* platform is the machine where we are compiling the JVM, the place where we run 'make'.
  - The *host* platform is the machine where the JVM will eventually run.
  - The *target* platform is the machine for which the compiler in the JVM generates code.

In this case the build platform is x86, and both the host and target are ARM. In all current
uses the host and target are the same, but the possibility that they could be different is
why we are stuck with talking about build and host platforms instead of something more
natural.

### JDKs

The build makes use of three JDKs.

  - The *bootstrap* JDK is required during early phases of the build that make use of Java.
  - The *build* JDK is required during later phases of the build when the JDK has to match the
version being built.
  - Finally there is the JDK that we're actually trying to create as a result of the build.

The bootstrap JDK is usually the previous major version to that being built, e.g. Java 8
when building Java 9. In the Docker image it is already installed in /usr/lib/jvm/java-8-openjdk-amd64
In this cross compile, the bootstrap JDK runs on the build platform (x86).

The build JDK would normally be built as a minimal JDK during the build itself, but this is not
working yet, so we have to provide one manually. There are two alternatives for doing so which
will be described below. The build JDK also runs on the build platform (x86).

The output JDK will be produced in the build/linux-arm-normal-server-release/images/jdk (and jre)
directories. It runs on the host platform (ARM).

### 1. Get the Source

:pencil: Temporary: Using my fork(s) instead of the official repos until the PRs are merged.

We can fetch the source code outside of Docker and mount it as a Docker volume later.

Make an empty working directory, cd into it, and fetch the OpenJDK extensions:

```
git clone https://github.com/JamesKingdon/openj9-openjdk-jdk9.git
```
cd into openj9-openjdk-jdk9 and switch to the arm branch
```
cd openj9-openjdk-jdk9/
git checkout arm
```

At this point you can choose between using my forks of openj9 and openj9-omr or the
official ones. The trade-off is that the offical repos will likely be more up to date,
but my forks may have ARM specific features that haven't been upstreamed yet. The official
forks will also not have been tested on ARM and may not work, where as my forks should
have had at least a minimal "does it run" level of testing.

To use my forks:

run get_source.sh with options to pull from my forks

```
bash get_source.sh -openj9-repo=https://github.com/JamesKingdon/openj9.git -openj9-branch=arm -omr-repo=https://github.com/JamesKingdon/openj9-omr.git -omr-branch=arm
```

OR

run get_source.sh with no options to get the offical repos

```
bash get_source.sh
```


### 2. Get a build JDK

There are two approaches to getting the build JDK; either download one from adoptopenjdk,
or compile your own using the same source code as will be used for the ARM JDK. It's
simpler and quicker to download a ready made one, so I'd recommend starting with that.

Download the latest "Linux x64" nightly build from 
https://adoptopenjdk.net/nightly.html?variant=openjdk9-openj9

into a temporary directory of your choice (or just put it in the working directory for simplicity)

e.g. at the time of writing,
```
wget https://github.com/AdoptOpenJDK/openjdk9-openj9-nightly/releases/download/jdk-9%2B181-20172711/OpenJDK9-OPENJ9_x64_Linux_20172711.tar.gz
```

Extract it into the working directory along side openj9-openjdk-jdk9

```
tar xf OpenJDK9-OPENJ9_x64_Linux_20172711.tar.gz
```
This should produce the directory jdk-9+181.

### 3. Create the Docker image

cd into openj9-openjdk-jdk9/openj9/buildenv/docker/jdk9/armhf_CC/arm-linux-gnueabihf and run

```
cd openj9-openjdk-jdk9/openj9/buildenv/docker/jdk9/armhf_CC/arm-linux-gnueabihf
docker build -t openj9arm .
```
### 4. Run the Docker image

The first time you run the Docker image you need to specifiy the volumes for the build JDK and
source code:

```
docker run -v <path to working dir>/openj9-openjdk-jdk9:/root/openj9-openjdk-jdk9 -v <path to working dir>/jdk-9+181:/root/buildjdk -it openj9arm
```
:pencil:
If you are using SELinux and can't access the volumes you may need to add :Z to the end of each mount string, e.g.
```
<path to working dir>/openj9-openjdk-jdk9:/root/openj9-openjdk-jdk9:Z
```

### 4a. Restarting an existing container

On subsequent usage, you can restart an existing container with the "docker start" command. This saves creating ever more containers that eventually need cleaning up.

First use docker ps -a to identify the container:

```
$ d ps -a
CONTAINER ID        IMAGE               COMMAND                  CREATED             STATUS                     PORTS               NAMES
51e37ca0a783        openj9arm           "/bin/bash"              2 days ago          Exited (0) 2 hours ago                         optimistic_goldberg
```
The container can be specified using either the container id field or the name field.
```
$ d start -i 51e37ca0a783
root@51e37ca0a783:~# 
```

Inside the Docker container you should see the buildjdk, freemarker.jar, the cross compiler tool chain and the openj9 source directory
```
root@51e37ca0a783:~# ls -l
total 796
drwxr-xr-x 10 root  root   4096 Nov 22 19:54 buildjdk
-rw-r--r--  1 root  root 802494 Jul  9  2006 freemarker.jar
drwxr-xr-x  9 11827 9000   4096 Nov 17 20:09 gcc-linaro-4.9.4-2017.01-x86_64_arm-linux-gnueabihf
drwxr-xr-x 17  1000 1000   4096 Nov 27 22:36 openj9-openjdk-jdk9
```

### 5. Run the build

Inside the Docker container...

cd into the openj9-openjdk-jdk9 directory and run the build script

```
cd /root/openj9-openjdk-jdk9/
./build-docker-arm
```

### 6. Test

The output is in the build/linux-arm-normal-server-release/images/jdk and jre
directories which the build script also creates zip archives from.
(Be careful not to pick up the linux-arm-normal-server-release/jdk directory - that's a 
temporary staging location that doesn't have the files in the standard places.)

scp one of jdk.zip or jre.zip to an ARM box, unzip and test with the following command-line (note the -Xjit:disableDirectToJNI -Xgcpolicy:optthruput options which work around problems known at the time of writing):

```
jkingdon@oban:/usbP2/sdks$ jdk/bin/java -Xjit:disableDirectToJNI -Xgcpolicy:optthruput -version
openjdk version "9-internal"
OpenJDK Runtime Environment (build 9-internal+0-adhoc..openj9-openjdk-jdk9)
Eclipse OpenJ9 VM (build 2.9, JRE 9 Linux arm-32 20171128_000000 (JIT enabled, AOT enabled)
OpenJ9   - c60bb3a
OMR      - e051fd4
OpenJDK  - a51ddb6 based on jdk-9+181)
```

### Separate Configure and Build steps

If you don't want to use the build-docker-arm script, you can perform the configure and
build steps individually.

Both stages are performed from the /root/openj9-openjdk-jdk9/ directory.

```
cd /root/openj9-openjdk-jdk9/
bash ./configure --openjdk-target=arm-linux-gnueabihf --with-abi-profile=armv6-vfp-hflt  --with-x=${OPENJ9_CC_DIR}/arm-linux-gnueabihf/ --with-freetype=${OPENJ9_CC_DIR}/arm-linux-gnueabihf/libc/usr/ --with-freemarker-jar=/root/freemarker.jar --with-build-jdk=/root/buildjdk
```

Hopefully this completes with a "Configuration summary:" similar to

```
Configuration summary:
* Debug level:    release
* HS debug level: product
* JDK variant:    normal
* JVM variants:   server
* OpenJDK target: OS: linux, CPU architecture: arm, address length: 32
* Version string: 9-internal+0-adhoc..openj9-openjdk-jdk9 (9-internal)

Tools summary:
* Boot JDK:       openjdk version "1.8.0_151" OpenJDK Runtime Environment (build 1.8.0_151-8u151-b12-0ubuntu0.16.04.2-b12) OpenJDK 64-Bit Server VM (build 25.151-b12, mixed mode)  (at /usr/lib/jvm/java-8-openjdk-amd64)
* Toolchain:      gcc (GNU Compiler Collection)
* C Compiler:     Version 4.9.4 (at /root/gcc-linaro-4.9.4-2017.01-x86_64_arm-linux-gnueabihf/bin/arm-linux-gnueabihf-gcc)
* C++ Compiler:   Version 4.9.4 (at /root/gcc-linaro-4.9.4-2017.01-x86_64_arm-linux-gnueabihf/bin/arm-linux-gnueabihf-g++)

Build performance summary:
* Cores to use:   8
* Memory limit:   32008 MB

The following warnings were produced. Repeated here for convenience:
WARNING: using cross tools not prefixed with host triplet
```
The final warning may be safely ignored (TODO why is it there?)

Next run make

```
make CONF=linux-arm-normal-server-release all
```
The build should finish with something similar to
```
Creating jre jimage
Creating jdk jimage
WARNING: Using incubator modules: jdk.incubator.httpclient
WARNING: Using incubator modules: jdk.incubator.httpclient
Cross compilation detected, skipping generated_target_rules_build
adding extra libs for arm cross compile
adding extra libs for arm cross compile
Cross compilation detected, skipping generated_target_rules_build
adding extra libs for arm cross compile
adding extra libs for arm cross compile
Stopping sjavac server
Finished building target 'all' in configuration 'linux-arm-normal-server-release'
```

### Hints

If things go wrong it's easy to end up with bad state in your build directory that
repeated makes won't recover from. If you suspect a mis-step has occurred, try
```
make CONF=linux-arm-normal-server-release dist-clean
```
and start over from the "Configure the build" step.

### The 'other' way of getting a build JDK

The Docker image for cross compiling is based on the one for x86 builds, so you can
use it to compile the source tree natively.

Note: First unset the OPENJ9_CC_PREFIX environment variable. At the moment it gets in the way of a native x86 build

```
unset OPENJ9_CC_PREFIX
```

cd to /root/openj9-openjdk-jdk9, and run configure for native build:

```
bash configure --with-freemarker-jar=/root/freemarker.jar
```

Compile all
```
make CONF=linux-x86_64-normal-server-release all
```

This should result in a jdk in build/linux-x86_64-normal-server-release/images/jdk
which can be used as the build jdk during the ARM configure step

```
  ... --with-build-jdk=/root/openj9-openjdk-jdk9/build/linux-x86_64-normal-server-release/images/jdk
```

Don't forget to reset the OPENJ9_CC_PREFIX env var before working on the ARM build again.

```
export OPENJ9_CC_PREFIX=arm-linux-gnueabihf
```
