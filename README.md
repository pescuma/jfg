# jfg 

## Introduction

The idea of this library is to create forms based on object atributes instead of having to write all the layout code by hand. To do that, it uses object reflection and a lightweight MVC model, where the programmer have only to write the model code.

Sometimes you are writing a POC or wants easy access to the attributes of some object (or even wants a simple form requesting some data from an user) but don't want the burden to write a GUI for that. In this cases jfg came at hand. It allows the creation of simple forms with 2 lines of code, and supports the basic java types, recursive objects (aka one object as a field of the other), static attributes (if it recives a _Class_)  and listeners for the model. It can apply the user input automatically or only when requested. Also, it tries to be very customizable.

For the view, it supports *SWT widgets*.

For the model, it supports *Reflection*, a *Map of fields* or *Custom attributes*.


## Overview

_(To run the examples you will need the SWT jars from http://www.eclipse.org/swt)_

The simplest way to use it is:
```
// Create the form
JfgFormComposite form = new JfgFormComposite(shell, SWT.NONE);

// Add elements to form
form.addContentsFrom(new ReflectionGroup(obj));
```
[full source code](https://github.com/pescuma/jfg/blob/master/examples/org/pescuma/jfg/examples/swt/SimpleForm.java)


You can use a Map:
```
// Create the object
Map<String, Object> map = new LinkedHashMap<String, Object>();
map.put("Text", "abcd");
map.put("Number", Integer.valueOf(0));
map.put("Checkbox", Boolean.TRUE);

// Create the form
final JfgFormComposite form = new JfgFormComposite(shell, SWT.NONE);

// Add elements to form
form.addContentsFrom(new MapGroup(map));
```
[full source code](https://github.com/pescuma/jfg/blob/master/examples/org/pescuma/jfg/examples/swt/MapDialog.java)


You can also add custom attributes:
```
// Create the form
JfgFormComposite form = new JfgFormComposite(shell, SWT.NONE);

// Add elements to form
form.add(new AbstractAttribute() {
    public String getName()
    {
        return "gui.name";
    }
    public Object getType()
    {
        return String.class;
    }
    public Object getValue()
    {
        return obj.getName();
    }
    public void setValue(Object value)
    {
        obj.setName((String) value);
    }
});
```
[full source code](https://github.com/pescuma/jfg/blob/master/examples/org/pescuma/jfg/examples/swt/SimpleForm.java)


## Documentation

 - [How it works](https://github.com/pescuma/jfg/wiki/How-it-works)
 - [GUI](https://github.com/pescuma/jfg/wiki/GUI)
 - [Attributes](https://github.com/pescuma/jfg/wiki/Attributes)


## Change log

```
. 0.4
  + Added Nebula CalendarCombo to display dates
  + Added @IgnoreInGUI
  + Option to mirror image in webcam
  * Lots of bug fixes

. 0.3
  + Added support for lists of objects
  + Added image control, including use of webcam (needs http://lti-civil.org but works without too)
  + Allows to add formaters to controls
  + Allows to add validation to controls
  + Easier way to configure controls
  + Allows to write custom layouts for controls
  + Includes my own fork of a split button (from http://swtsplitbutton.googlecode.com )

. 0.2
  * Renamed package to org.pescuma.jfg
  + Hides transient and synthetic fields
  + SWT: Added handling of File attributes
  + SWT: Uses Labels and Composites created through ComponentFactory
  * Fix for method name parsing

. 0.1
  Initial version
```
