// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,

  HrURLprefix: 'http://172.23.234.59:8094/hr',
  employeeURLprefix: 'http://172.23.234.85:8093/quiz/test',
  adminURLprefix:'http://172.23.234.54:8092/admin',
  authURLprefix:'http://172.23.239.75:8091/auth',
  fetchTestURLprefix:'http://172.23.234.85:8093/quiz/test/questions/'
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.
