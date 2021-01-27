import React from 'react';
import { sendWsMessage, setWsHandler } from './websocket';
import UserList from './UserList';
import UserBar from './UserBar';
import ChatBox from './ChatBox';
import {Grid} from '@material-ui/core';
import { ChatContainer, ConversationList, Conversation, Avatar, MainContainer, Sidebar, Search } from "@chatscope/chat-ui-kit-react";

class App extends React.Component {
	
	state = {
		message: '',
		messages: [], 
		conversationList: [], 
		searchUserList: [],
		isSearching: false,
		chatList: [], 
		currentChat: null
	};

	constructor(props) {
		super(props);

		setWsHandler('NEW_MESSAGE', message => {

			this.setState(prevState => ({
				messages: [...prevState.messages, message]
			}));
		});
		
		setWsHandler('USER_SEARCH', message => {

			this.setState({searchUserList: message.users});
		});
	}
	
	handleSearchChange = (value) => {
		console.log(value);
		if (value !== '' && value != undefined) {
			this.setState({isSearching: true});
			sendWsMessage(JSON.stringify({'msgType': 'USER_SEARCH' , 'userId': this.props.ctx.userId, 'data': value }));
		}
		else {
			this.setState({isSearching: false});
		}
	}
	
	handleSearchClear = () => {
		this.setState({isSearching: false});
	}
	
	handleInputSelection = (event, value, reason) => {
		if (reason === 'select-option') {
			this.setState({currentChat : value});
		}
	}
	
	onInputChange = text => {
		this.setState({'message': text });
	}
	
	changeCurrentChat = (event) => {
		console.log(event.target.getAttribute('userId'));
		
	}
	
	sendMessage = event => {
		sendWsMessage(JSON.stringify({'msgType': 'NEW_MESSAGE', 'userId': this.props.ctx.userId, 'data': this.state.message }));
	}
	
	render() {
		
		const messages = this.state.messages.map((message) => {
			return (
				<p>{JSON.stringify(message)}</p>
			);
		});
		
		const searchUserList = this.state.searchUserList.map((user) => {
			return (
				<Conversation name={user.fullName} lastSenderName={user.fullName} onClick={this.changeCurrentChat} userId={user.userId}>
					<Avatar src={user.portraitUrl} name={user.fullName} />
				</Conversation>
			);
		});
		
		const conversationList = this.state.conversationList.map((user) => {
			return (
				<Conversation name={user.fullName} lastSenderName={user.fullName} >
					<Avatar src={user.portraitUrl} name={user.fullName} />
				</Conversation>
			);
		});
		
		return (
			<div className='chat'>
				<div style={{ position: "relative", height: "500px" }}>
					<MainContainer responsive>	
						<Sidebar position="left" scrollable={false}>
							<Search 
								placeholder="Search..." 
								onChange={this.handleSearchChange}
								onClearClick={this.handleSearchClear}
							/>
							<ConversationList> 
								{this.state.isSearching?(
									searchUserList
								):(
									conversationList
								)}
							</ConversationList>
						</Sidebar>
						<ChatBox messages={this.state.messages} sendMessage={this.sendMessage} onInputChange={this.onInputChange}/>
					</MainContainer>
				</div>
			</div>
		);
	}
}

export default App;