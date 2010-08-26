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
You will most definitely need Windows, Eclipse 3.5+, BlackBerry Eclipse Plugin, BlackBerry Eclipse Component 
Packs, Ant, Ant-Contrib, and BB Ant Tools.

Building PhoneGap BlackBerry Projects
-------------------------------------------------------------

In general the process of building a PhoneGap app consists of building the PhoneGap BlackBerry library 
and loading that onto your device / simulator and then building your BlackBerry with your main 
UiApplication class inheriting from com.phonegap.PhoneGap. Here are the details.

1. Clone the PhoneGap repository.
2. Edit the common.properties and project.properties files in phonegap-blackberry/framework. The paths in common.properties need to match your environment setup and the project.properties defines your project name etc. Most important of these is to point jde.home in common.properties to point to one of the Eclipse component packs that you have already installed (e.g. C:\\Program Files\\eclipse\\plugins\\net.rim.ejde.componentpack4.7.0_4.7.0.57\\components)
3. Open up a command prompt at the phonegap-blackberry folder.
4. Run 'ant' and see all the options you have. If your jde.home path is not correct you should see an error about it.
5. Run 'ant create -Dapp.name=MyApp -Doutput.dir=My/Relative/App/Dir -Dpackage=com.foobar' from the command-line. It will build the PhoneGap BlackBerry lib, create a new phonegap.js file and create a new project folder in the output.dir folder. The project folder could then be opened in Eclipse if it is so desired (see below).
6. From the command prompt again change to the output.dir folder where you can now run 'ant load-simulator' and that will build your new application and load it (along with the PhoneGapBlackBerryLib.cod) on the default simulator. There are other tasks that you can also run:
   a) 'ant sign': Runs the 'build' task first, and then runs the signature tool on the compiled binary. Make sure to specify the 'password' property at the top of the build.xml file, otherwise the signature tool will fail!
   b) 'ant load-simulator': Runs the 'sign' task first, then copies the signed binaries over to the simulator directory you specified at the top of the build.xml and finally runs the simulator. You should see your application under the BB Menu -> Downloads.
   c) 'ant load-device':	Runs the 'sign' task first, then executes the javaloader tool to load the signed binaries onto an attached (via USB) device.

Building PhoneGap BlackBerry Projects from Eclipse
-------------------------------------------------------------

If you want to run things from Eclipse you can do that too. Of course it helps a lot with debugging.

1. Launch Eclipse, go to File->New->BlackBerry project.
2. Choose "Use Existing Source" and navigate to the folder that you specified as the output.dir in step 5 above.
3. Modify the contents of the "www" directory to add your own HTML, CSS and Javascript.
4. Create a linked folder in the src folder to the www folder, this is important otherwise Eclipse will not deploy your www folder to the device / simulator.
5. Run or debug from Eclipse as desired. NOTE: Whatever simulator or device you are running / debugging on must have the PhoneGapBlackBerryLib.cod on that device - this is most easily achieved by running 'ant load-simulator' or load-device as in step 6 above.
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