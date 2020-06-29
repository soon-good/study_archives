console.log("======= SAMPLE =======");
const {getGreetingEn, getGreetingKr} = require("./greetings");
// import { getGreetingEn, getGreetingKr } from "./greetings";

console.log(getGreetingEn());
console.log(getGreetingKr());

console.log('');
console.log('======= OLD STYLE =======');
var greetings = require("./greetings");
console.log(greetings.getGreetingEn());
console.log(greetings.getGreetingKr());

const getGreetingKrAndEn = require('./concat')
console.log('');
console.log('======= CONCAT =======');
console.log(getGreetingKrAndEn());
