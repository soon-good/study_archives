const {getGreetingEn, getGreetingKr} = require("./greetings");

function getGreetingKrAndEn(){
    return getGreetingEn() + ":: (한국어) >>> " + getGreetingKr();
}

module.exports = getGreetingKrAndEn;