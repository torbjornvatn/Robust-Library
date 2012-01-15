   Name
   ----
   Robust Library v. 0.1. 
   
   Developers
   ----------
   Konrad Ku¸akowski (konrad(at)kulakowski.org) maintainer/developer (most of the code)
   Jacek Rzucid¸o (Light Sensor handling)
   
      
   Motivation & Description
   ------------------------
   Lego Mindstorms NXT hardware resources are limited and usually 
   are not enough to handle complex control algorithms using knowledge base 
   and world model. A natural way of overcoming the problem is to move 
   the control algorithm from the NXT intelligent brick to a PC computer 
   and control the brick remotely via BT. In this approach every single reading 
   of a sensor has to be passed on via a BT link, 
   which makes the frequent sensor readings extremely ineffective.
   The Robust library is designed as a Lejos/Java based cure for this problem. 
   It supports asynchronous communication for almost all standard sensors 
   such as ultrasonic, sound, touch and light sensor and some third party sensors 
   such as a magnetic compass or camera. 
   It also facilitates control of servomechanisms and vision subsystem.
   
   Note that currently library has support for classical two wheeled vehicles 
   such as TriBot. Legged robots are not supported, however probably it would not be 
   a big deal to add this support.
   
   It is not "new" ICommand, since it benefits from different (asynchronous) 
   communication paradigm.

   Build
   -----
   Be warned: my description might be a bit unix oriented, since I am using Robust on
   Linux and Mac. Any help (including documentation) as regards the Windows platform 
   is appreciated! (I do not use Windows). 
   
   Robust library consists of two modules: robustpc and robustnxt. 
   Both of them depends on Lejos (http://lejos.sourceforge.net/)
   so first download and install Lejos (do not forget setup environment
   variables such as PATH and NXJ_HOME), next enter the robust distribution
   directory and:
   
   * call 'ant' to build robustpc module. In case of success robustpc.jar is built
   * enter the directory srcnxt and call build.sh. In case of success robustnxt.nxj 
   is built
       
   Install & Use
   -------------
   In order to use Robust Library, you have to install Lejos firmware on NXT Brick
   (consult Lejos tutorial http://lejos.sourceforge.net/nxt/nxj/tutorial/index.htm)
   Next install robustnxt.nxj by call in build directory: 
   
   nxjupload -b -d 01:23:45:67:89:01 robustnxt  
   (01:23:45:67:89:01 is a bluetooth adress of your Mindstorms brick
   
   In the root directory you may found robustcfg.xml file. Please adjust them to your
   Lego Mindstorms vehicle. 
   
   To start use Robust you need run robustnxt on NXT Brick, and run your control 
   application using robustpc.jar library. To see how the library might be used 
   please follow examples (see robust.pc.examples.*)
   
   TODOs
   -----
   
   Oh my Goddness! Be honest I see many places where the library can be improved.
   Some of them (but not all of them) are:
   * make the sensors and motors ports configurable
   * rewrite message queues using ArrayList (it was not available in Lejos 0.6)
   * refactor RobustNXT class. Probably it should be splited into a few several classes
     the same for NXTBTMsg
   * write more complex examples
   * write a documentation 
    
   Copyright
   ---------
   Copyright (C) 2008-2009 Konrad Kulakowski (www.kulakowski.org) 
   and all other people mentioned as developers. 
   (Feel invited to be a developer :-)

   Robust library is free software; you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation; either version 3, or (at your option)
   any later version.

   Robust library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program; see the file license.txt.
   If not, write to the Free Software Foundation,
   59 Temple Place - Suite 330, Boston, MA 02111-1307, USA. 