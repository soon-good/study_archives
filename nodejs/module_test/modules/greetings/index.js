import {greetingEnMsg, fnGreetingEnMsg} from './greetingEn';

const printSomeMessage = () => {
    console.log(greetingEnMsg);
    fnGreetingEnMsg();
};

const mergeAll = () => {
    return {
        fnGreetingEnMsg, printSomeMessage, greetingEnMsg
    }
}

export default mergeAll();