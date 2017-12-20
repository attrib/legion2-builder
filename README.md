# Legion TD 2 Builder

[![Build Status](https://travis-ci.org/attrib/legion2-builder.svg?branch=fromTools)](https://travis-ci.org/attrib/legion2-builder)

See live version [attrib.github.io](https://attrib.github.io)

Test your build before playing [Legion 2 TD](https://legiontd2.com)

All images and data is copyrighted by [Legion 2 TD](https://legiontd2.com)

## Working

* Read values from Legion 2 TD map file (currently 1.65)
* Add fighters to lane
* Add mercenary for income calculation
* Calculate damage dealt by wave and fighters and predict a leak probability
* Using unit icons 
* Add attack and defense type
* Some refactoring (more modular)
* Upgrade unit
* Sell unit
* Share builds
* Read placement from replay file

## ToDo

Full list see [Issues](https://github.com/attrib/legion2-builder/issues)

* Implement unit placement
* Load / Save builds from session store
* Better design (PRs welcome)
* Improved replay reader
* Any graph requests?
* Battle calculator

## About the project

This project was original bootstrapped with [Create React Kotlin App](https://github.com/JetBrains/create-react-kotlin-app) and is now ejected to use gradle to import some other libs from Kotlin world.

Its a fun project for me to play with Kotlin and React.

Development server: `./gradlew run -t`

Build: `./gradlew assemble`

## Collaborators

* attrib
* synopia
