PhoneGap BlackBerry
=============================================================
Allows developers to create BlackBerry applications using HTML, 
CSS and JavaScript, and bridge into device functionality like 
geolocation, SMS, device information, accelerometer, etc. via
a JavaScript API.

Pre-requisites
-------------------------------------------------------------
Your best bet is to check the PhoneGap wiki for detailed
installation and setup instructions: 
http://phonegap.pbworks.com/Getting+Started+with+PhoneGap+(BlackBerry)

Create a PhoneGap project with Eclipse
-------------------------------------------------------------
1. Launch Eclipse, go to File->Import->Existing BlackBerry project.
2. Navigate over to where you cloned the git repo, and point it to the phonegap.jdp file located in blackberry/framework/.
3. Modify the contents of the "www" directory to add your own HTML, CSS and Javascript.
4. Before running, right-click on project root and make sure 'Activate for BlackBerry' is checked.
5. Run or debug from Eclipse as desired.
6. When you are satisfied with the application, make sure you sign it! (BlackBerry menu -> Request Signatures)
   This step needs to be done every time you change your source code. If you are running in simulator, you do not need
   to sign your binaries - it is only necessary for deployment to actual devices.
7. A few ways to deploy to device:
   a) Right-click on the project root in Eclipse and click on 'Generate ALX file.' You can then use this
      file in RIM's Desktop Manager software to load the binaries directly onto the device.
   b) Use the javaloader.exe program that comes with the JDE component packs to load directly onto device. Usage:
      javaloader -u load path/to/codfile.cod
	  The -u parameter specifies loading via USB.
   c) Over-the-air installation. Set up your application .jad, .jar and .cod files onto a web server. See RIM's documentation
      for more details or this succinct PDF for info: http://assets.handango.com/marketing/developerTeam/BlackBerryOTADeployment.pdf

Building PhoneGap BlackBerry Projects with Apache Ant
-------------------------------------------------------------
You'll need all the prerequisites listed by BB Ant Tools (http://bb-ant-tools.sourceforge.net/) and Ant-Contrib. Both are in the phonegap-blackberry/util folder. If you want access to building using Ant from Eclipse,
check out http://www.slashdev.ca/2007/05/30/blackberry-development-with-ant-eclipse/ for instructions on how to do it.

1. Clone the PhoneGap repository.
2. Edit the common.properties and project.properties files in phonegap-blackberry/framework. The paths in common.properties need to match your environment setup and the project.properties defines your project name etc.
3. Open up a command-line to the phonegap-blackberry folder.
4. Run 'ant create -Dapp.name=MyApp -Doutput.dir=My/Relative/App/Dir -Dpackage=com.foobar' from the command-line. It'll build the PhoneGap BlackBerry lib and create a new phonegap.js file to be used in your BlackBerry application and create a new project folder in the output.dir folder. The project folder could then be opened in Eclipse if it is so desired.
5. From the command-line again change to the output.dir folder where you can now run 'ant load-simulator' and that will build your new application and load it on the default simulator. There are other tasks that you can also run:
   a) 'ant sign': Runs the 'build' task first, and then runs the signature tool on the compiled binary. Make sure to specify the 'password' property at the top of the build.xml file, otherwise the signature tool will fail!
   b) 'ant load-simulator': Runs the 'sign' task first, then copies the signed binaries over to the simulator directory you specified at the top of the build.xml and finally runs the simulator. You should see your application under the BB Menu -> Downloads.
   c) 'ant load-device':	Runs the 'sign' task first, then executes the javaloader tool to load the signed binaries onto an attached (via USB) device.