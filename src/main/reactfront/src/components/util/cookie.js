import { Cookies } from 'react-cookie';

const cookies = new Cookies();

export const setCookie = (name, value, options = {}) => {
    const defaultOptions = { path: '/', maxAge: 10 * 24 * 60 * 60 };
    const mergedOptions = { ...defaultOptions, ...options };
    cookies.set(name, value, mergedOptions);
};

export const getCookie = (name) => {
    return cookies.get(name);
};

export const getAll=()=>{
    return cookies.getAll()
}

export const eraseCookie = (name, options = {}) => {
    const defaultOptions = { path: '/' };
    const mergedOptions = { ...defaultOptions, ...options };
    cookies.remove(name, mergedOptions);
};

export const logout = () => {
    eraseCookie('jwtToken');
    eraseCookie('id');
};
