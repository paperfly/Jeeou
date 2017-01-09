# InstantJio
An easier way to 'jio' (invite) your friends!

## How does it look like?
<img src="screenshots/Screenshot_Login.png" alt="Login UI" width="33%">
<img src="screenshots/Screenshot_Groups.png" alt="Groups UI" width="33%">
<img src="screenshots/Screenshot_Contacts.png" alt="ContactsUI" width="33%">

## Overview
This is a side project for us (Paperfly) to get us started to be familiar with Android application development.

The idea is a blend of WhatsApp, Eventbrite and Google Calendar, but on a smaller scale and target audience, specifically, your friends and family.

## Implemented features
1. Start a quick event* with 2 taps (3 if you count opening the app as one)
2. Create groups with your friends
3. Start an event with your friends or a group

*Quick events are events that are planned ahead by the user, that is repeatable and reusable, like a template, e.g. basketball night every Friday

## TODOs
1. Fix bugs
   * EventFragment doesn't list upcoming events
   * ContactsFragment doesn't list contacts unless in some odd situations
   * Properly handle all activity lifecycles
2. Implement layered architecture to prevent tight coupling
    * Firebase SDK is referenced everywhere in every file, as such, a data layer or DAO is needed to make upgrading the SDK in the future easier
3. Implement MVVM using data binding library
4. Use Butter Knife library
5. Code refactoring
    * Naming convention
    * Conformance to Android coding style
    * Refactor ugly/spaghetti code
6. Implement new features in the backlog

## Backlog
1. Video invite
2. Chat support?
3. GPS location support

## Technical documentation
### Dependencies or libaries used
1. Support library
2. Firebase (https://firebase.google.com/)
3. Google Play Services
4. Guava (https://github.com/google/guava)
5. Picasso (http://square.github.io/picasso/)
6. CircleImageView (https://github.com/hdodenhof/CircleImageView)

### Design wiki
TBC

## Disclaimer
As stated in the overview section, this project is in no way a commercial project, eventhough it will be published on Google Play Store. We don't plan to expand or scale this app in the foreseeable future.
