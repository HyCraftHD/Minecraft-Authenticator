# Changelog
All notable changes to this project will be documented in this file.

## [3.0.6] - 2023-09-14
### Changed
 - Fixed missing opens directive in module-info. Thanks shirosaka

## [3.0.5] - 2023-05-19
### Added
 - Added callback for login states

## [3.0.4] - 2023-03-04
### Added
 - Fix gson dependency publishing in maven
 - Expose function package in module

## [3.0.3] - 2023-03-02
### Added
 - Jar artifact is now signed
 - Add module file

## [3.0.2] - 2022-02-24
### Added
 - Added option to attach extra properties to an authentication file

## [3.0.1] - 2022-02-22
### Added
 - Added client secret support for azure applications. Thanks AppleDash!

## [3.0.0] - 2022-02-20
### Changed
 - Rewritten most parts of the library
 - Fixed bug that you can lose the refresh token when an error occurred
 - Removed yggdrasil authentication as it will be shut down

### Added
 - Added xuid and clientId to the user object
 - Added option to retrieve xbox profile settings

## [2.0.1] - 2022-01-28
### Changed
 - Removed guava dependency (replaced url encoders with jdk ones)
 - Updated gson dependency to 2.8.9

## [2.0.0] - 2022-01-28
### Added
 - Added timeout values in authentication builder
 
### Changed
 - Cleanup javadocs
 - Update to java 17
 - Update dependencies

## [1.0.1] - 2021-06-30
### Added
 - Added support for custom azure applications
 
### Changed
 - Remove dependency for jopt-simple and removed Main class as it should be a test class

## [1.0.0] - 2021-05-20
### Added
 - Added yggdrasil and microsoft authentication