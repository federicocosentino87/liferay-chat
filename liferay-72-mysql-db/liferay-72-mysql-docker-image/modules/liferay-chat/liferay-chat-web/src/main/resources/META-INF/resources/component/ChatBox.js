import React from 'react';
import {Grid, Paper, List} from '@material-ui/core';
import {
  MainContainer,
  ChatContainer,
  MessageList,
  Message,
  MessageInput,
} from "@chatscope/chat-ui-kit-react";


class ChatBox extends React.Component {
	
	
	render() {
		const messages = this.props.messages.map((message) => {
			return (
				<Message
					model={{
						message: message.data,
						sentTime: "just now",
						sender: "Joe",
					}}
				/>
			);
		});
		return (
			<ChatContainer>
				<MessageList>
					{messages}
				</MessageList>
				<MessageInput 
					placeholder="Type message here" 
					attachButton={false} 
					onSend={this.props.sendMessage} 
					onChange={this.props.onInputChange} 
				/>
			</ChatContainer>
		);
	}
}

export default ChatBox;