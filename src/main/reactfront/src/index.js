import React from 'react';
import ReactDOM from 'react-dom/client';
//import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';
import { QueryClient,QueryClientProvider } from 'react-query';
const queryClient=new QueryClient({
    defaultOptions:{
        queries:{
            refetchOnWindowFocus: false,
            refetchOnMount: false,
            refetchOnReconnect: false,
            retry: false,
            staleTime: 5 * 60 * 1000, // 5분 (밀리초 단위)
            cacheTime: 24 * 60 * 60 * 1000, // 24시간 (밀리초 단위)
        }
    }
})
const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
    <QueryClientProvider client={queryClient}>
        <App />
    </QueryClientProvider>
);

reportWebVitals();
