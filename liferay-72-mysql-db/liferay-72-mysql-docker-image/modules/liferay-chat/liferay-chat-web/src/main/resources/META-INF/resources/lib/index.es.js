import React from 'react';
import ReactDOM from 'react-dom';
import App from '../component/App';
import { initWs } from '../component/websocket';

export default function(elementId, ctx) {
	initWs(ctx);
	
	ReactDOM.render(
		<App ctx={ctx} />, 
		document.getElementById(elementId)
	);
}