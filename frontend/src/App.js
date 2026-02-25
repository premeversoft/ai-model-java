import React, { useState, useRef, useEffect } from 'react';
import axios from 'axios';
import ReactMarkdown from 'react-markdown';
import './App.css';

const API_URL = process.env.REACT_APP_API_URL || '/openai/api/chat';
const API_URL_WITH_ROLES = '/openai/api/chat-with-roles';
const API_URL_TEMPLATES = '/openai/api/prompt-templates';
const API_URL_ADVANCED = '/openai/api/chat-advanced';
const API_URL_STREAM = '/openai/api/chat-stream';
const API_URL_STRUCTURED = '/openai/api/structured/answer';

function App() {
  const [messages, setMessages] = useState([
    { role: 'assistant', content: 'Hello! I\'m your AI assistant. Choose a model and start chatting!' }
  ]);
  const [input, setInput] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [selectedModel, setSelectedModel] = useState('ollama');
  const [systemPrompt, setSystemPrompt] = useState('');
  const [showSystemPrompt, setShowSystemPrompt] = useState(false);
  const [useRoles, setUseRoles] = useState(false);
  const [showOptions, setShowOptions] = useState(false);
  const [chatOptions, setChatOptions] = useState({
    temperature: 0.7,
    maxTokens: 256,
    topP: 0.9,
    presencePenalty: 0,
    stream: false,
    structured: false
  });
  const [templates, setTemplates] = useState([]);
  const [selectedTemplateId, setSelectedTemplateId] = useState('');
  const [templateVars, setTemplateVars] = useState({});
  const messagesEndRef = useRef(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  useEffect(() => {
    const fetchTemplates = async () => {
      try {
        const response = await axios.get(API_URL_TEMPLATES);
        setTemplates(Array.isArray(response.data) ? response.data : []);
      } catch (error) {
        setTemplates([]);
      }
    };

    fetchTemplates();
  }, []);

  const formatStructuredResponse = (data) => {
    let formatted = '';
    if (data.topic) formatted += `## 📌 ${data.topic}\n\n`;
    if (data.summary) formatted += `**Summary:**\n${data.summary}\n\n`;
    if (data.keyPoints?.length) {
      formatted += `**🔑 Key Points:**\n${data.keyPoints.map(p => `- ${p}`).join('\n')}\n\n`;
    }
    if (data.steps?.length) {
      formatted += `**📝 Steps:**\n${data.steps.map((s, i) => `${i + 1}. ${s}`).join('\n')}\n\n`;
    }
    if (data.risks?.length) {
      formatted += `**⚠️ Risks:**\n${data.risks.map(r => `- ${r}`).join('\n')}\n\n`;
    }
    if (data.references?.length) {
      formatted += `**📚 References:**\n${data.references.map(r => `- ${r}`).join('\n')}`;
    }
    return formatted.trim() || JSON.stringify(data, null, 2);
  };

  const sendMessage = async (e) => {
    e.preventDefault();
    if (!input.trim() || isLoading) return;

    const userMessage = input.trim();
    setInput('');
    
    // Add user message
    setMessages(prev => [...prev, { role: 'user', content: userMessage }]);
    setIsLoading(true);

    try {
      const conversationHistory = useRoles
        ? messages.slice(1).map(msg => ({ role: msg.role, content: msg.content }))
        : [];

      const payload = {
        message: userMessage,
        model: selectedModel,
        systemPrompt: useRoles ? systemPrompt : '',
        conversationHistory: conversationHistory,
        options: {
          temperature: Number(chatOptions.temperature),
          maxTokens: Number(chatOptions.maxTokens),
          topP: Number(chatOptions.topP),
          presencePenalty: Number(chatOptions.presencePenalty),
          stream: Boolean(chatOptions.stream)
        }
      };

      if (chatOptions.structured) {
        const response = await axios.post(API_URL_STRUCTURED, {
          message: userMessage,
          model: selectedModel
        });
        const formatted = formatStructuredResponse(response.data);
        setMessages(prev => [...prev, { role: 'assistant', content: formatted }]);
      } else if (chatOptions.stream) {
        const assistantIndex = messages.length + 1;
        setMessages(prev => [...prev, { role: 'assistant', content: '' }]);
        await streamResponse(payload, assistantIndex);
      } else {
        const response = await axios.post(useRoles ? API_URL_WITH_ROLES : API_URL_ADVANCED, payload);
        setMessages(prev => [...prev, { role: 'assistant', content: response.data }]);
      }
    } catch (error) {
      console.error('Error:', error);
      setMessages(prev => [...prev, { 
        role: 'assistant', 
        content: '❌ Sorry, there was an error processing your request. Please make sure the backend is running.' 
      }]);
    } finally {
      setIsLoading(false);
    }
  };

  const streamResponse = async (payload, assistantIndex) => {
    const response = await fetch(API_URL_STREAM, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(payload)
    });

    if (!response.ok || !response.body) {
      throw new Error('Streaming response failed');
    }

    const reader = response.body.getReader();
    const decoder = new TextDecoder('utf-8');
    let buffer = '';

    while (true) {
      const { value, done } = await reader.read();
      if (done) break;
      buffer += decoder.decode(value, { stream: true });
      const lines = buffer.split('\n');
      buffer = lines.pop() || '';

      lines.forEach((line) => {
        if (line.startsWith('data:')) {
          const chunk = line.replace('data:', '').trimStart();
          if (chunk && chunk !== '[DONE]') {
            setMessages(prev => {
              const updated = [...prev];
              const target = updated[assistantIndex];
              if (target) {
                target.content = `${target.content}${chunk}`;
              }
              return updated;
            });
          }
        }
      });
    }
  };


  const clearChat = () => {
    setMessages([
      { role: 'assistant', content: 'Hello! I\'m your AI assistant. Choose a model and start chatting!' }
    ]);
  };

  const applySystemPrompt = (promptText) => {
    setSystemPrompt(promptText);
    setUseRoles(true);
    setShowSystemPrompt(false);
    setMessages([
      { role: 'assistant', content: 'System prompt applied! I will follow these instructions in our conversation.' }
    ]);
  };

  const systemPromptPresets = [
    { name: 'Code Assistant', prompt: 'You are an expert programmer. Provide clear, concise code examples with explanations. Use best practices and modern coding standards.' },
    { name: 'Professional Writer', prompt: 'You are a professional writer. Write in a clear, engaging, and formal style. Focus on proper grammar and structure.' },
    { name: 'Teacher', prompt: 'You are a patient and knowledgeable teacher. Explain concepts step by step in simple terms. Use examples to illustrate your points.' },
    { name: 'Creative Assistant', prompt: 'You are a creative assistant. Think outside the box and provide innovative, imaginative solutions and ideas.' },
  ];

  const selectedTemplate = templates.find((template) => template.id === selectedTemplateId);
  const hasMissingRequired = selectedTemplate?.variables?.some(
    (variable) => variable.required && !String(templateVars[variable.name] || '').trim()
  );

  const updateTemplateVar = (name, value) => {
    setTemplateVars((prev) => ({ ...prev, [name]: value }));
  };

  const renderTemplate = (templateText, values) =>
    templateText.replace(/\{(\w+)\}/g, (match, key) => (values[key] != null ? values[key] : ''));

  const filledTemplatePreview = selectedTemplate
    ? renderTemplate(selectedTemplate.template, templateVars)
    : '';

  const applySelectedTemplate = () => {
    if (!selectedTemplate) return;
    const filledTemplate = renderTemplate(selectedTemplate.template, templateVars).trim();
    if (!filledTemplate) return;
    applySystemPrompt(filledTemplate);
  };

  return (
    <div className="App">
      <header className="app-header">
        <div className="header-content">
          <h1>🤖 Spring AI Chat</h1>
          <div className="header-info">
            <select 
              value={selectedModel} 
              onChange={(e) => setSelectedModel(e.target.value)}
              className="model-selector"
            >
              <option value="ollama">Ollama (TinyLlama)</option>
              <option value="openai">OpenAI (GPT-3.5)</option>
            </select>
            <button 
              onClick={() => setShowSystemPrompt(!showSystemPrompt)}
              className="system-prompt-btn"
              title="Set system prompt (role)"
            >
              {useRoles ? '✅' : '⚙️'} Roles
            </button>
            <button
              onClick={() => setShowOptions(!showOptions)}
              className="system-prompt-btn"
              title="Chat options"
            >
              ⚙️ Options
            </button>
          </div>
        </div>
        <button onClick={clearChat} className="clear-btn">
          Clear Chat
        </button>
      </header>

      {showOptions && (
        <div className="options-panel">
          <h3>Chat Options</h3>
          <div className="options-grid">
            <label>
              Temperature
              <input
                type="number"
                step="0.1"
                min="0"
                max="2"
                value={chatOptions.temperature}
                onChange={(e) => setChatOptions(prev => ({ ...prev, temperature: e.target.value }))}
              />
            </label>
            <label>
              Max Tokens
              <input
                type="number"
                step="1"
                min="1"
                max="2048"
                value={chatOptions.maxTokens}
                onChange={(e) => setChatOptions(prev => ({ ...prev, maxTokens: e.target.value }))}
              />
            </label>
            <label>
              Top P
              <input
                type="number"
                step="0.1"
                min="0"
                max="1"
                value={chatOptions.topP}
                onChange={(e) => setChatOptions(prev => ({ ...prev, topP: e.target.value }))}
              />
            </label>
            <label>
              Presence Penalty
              <input
                type="number"
                step="0.1"
                min="-2"
                max="2"
                value={chatOptions.presencePenalty}
                onChange={(e) => setChatOptions(prev => ({ ...prev, presencePenalty: e.target.value }))}
              />
            </label>
            <label className="options-toggle">
              Streaming
              <input
                type="checkbox"
                checked={chatOptions.stream}
                onChange={(e) => setChatOptions(prev => ({ ...prev, stream: e.target.checked }))}
              />
            </label>
            <label className="options-toggle">
              Structured Output
              <input
                type="checkbox"
                checked={chatOptions.structured}
                onChange={(e) => setChatOptions(prev => ({ ...prev, structured: e.target.checked }))}
              />
            </label>
          </div>
        </div>
      )}

      {showSystemPrompt && (
        <div className="system-prompt-panel">
          <h3>Set System Prompt (Defines AI Role & Behavior)</h3>
          <div className="template-panel">
            <h4>Prompt Templates</h4>
            {templates.length === 0 ? (
              <p className="template-empty">No templates found. Use presets or a custom prompt below.</p>
            ) : (
              <>
                <select
                  value={selectedTemplateId}
                  onChange={(e) => {
                    setSelectedTemplateId(e.target.value);
                    setTemplateVars({});
                  }}
                  className="template-selector"
                >
                  <option value="">Select a template</option>
                  {templates.map((template) => (
                    <option key={template.id} value={template.id}>
                      {template.name}
                    </option>
                  ))}
                </select>
                {selectedTemplate && (
                  <div className="template-details">
                    <p className="template-description">{selectedTemplate.description}</p>
                    <div className="template-variables">
                      {(selectedTemplate.variables || []).map((variable) => {
                        const isLongText = ['code', 'text'].includes(variable.name);
                        return (
                          <label key={variable.name} className="template-field">
                            <span>
                              {variable.label}
                              {variable.required ? ' *' : ''}
                            </span>
                            {isLongText ? (
                              <textarea
                                value={templateVars[variable.name] || ''}
                                onChange={(e) => updateTemplateVar(variable.name, e.target.value)}
                                placeholder={variable.placeholder}
                                rows="3"
                              />
                            ) : (
                              <input
                                type="text"
                                value={templateVars[variable.name] || ''}
                                onChange={(e) => updateTemplateVar(variable.name, e.target.value)}
                                placeholder={variable.placeholder}
                              />
                            )}
                          </label>
                        );
                      })}
                    </div>
                    <div className="template-preview">
                      <div className="template-preview-header">Preview</div>
                      <pre>{filledTemplatePreview || 'Fill the fields to preview the final prompt.'}</pre>
                    </div>
                    <div className="template-actions">
                      <button
                        onClick={applySelectedTemplate}
                        className="apply-btn"
                        disabled={hasMissingRequired}
                      >
                        Apply Template
                      </button>
                    </div>
                  </div>
                )}
              </>
            )}
          </div>
          <div className="preset-buttons">
            {systemPromptPresets.map((preset, idx) => (
              <button 
                key={idx}
                onClick={() => applySystemPrompt(preset.prompt)}
                className="preset-btn"
              >
                {preset.name}
              </button>
            ))}
          </div>
          <div className="custom-prompt">
            <textarea
              value={systemPrompt}
              onChange={(e) => setSystemPrompt(e.target.value)}
              placeholder="Or write your custom system prompt here..."
              className="system-prompt-input"
              rows="3"
            />
            <div className="prompt-actions">
              <button 
                onClick={() => applySystemPrompt(systemPrompt)}
                className="apply-btn"
                disabled={!systemPrompt.trim()}
              >
                Apply Custom Prompt
              </button>
              <button 
                onClick={() => setShowSystemPrompt(false)}
                className="cancel-btn"
              >
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}

      <div className="chat-container">
        <div className="messages">
          {messages.map((message, index) => (
            <div key={index} className={`message ${message.role}`}>
              <div className="message-avatar">
                {message.role === 'user' ? '👤' : '🤖'}
              </div>
              <div className="message-content">
                <div className="message-role">
                  {message.role === 'user' ? 'You' : 'AI Assistant'}
                </div>
                <div className="message-text">
                  <ReactMarkdown>{message.content}</ReactMarkdown>
                </div>
              </div>
            </div>
          ))}
          {isLoading && (
            <div className="message assistant">
              <div className="message-avatar">🤖</div>
              <div className="message-content">
                <div className="message-role">AI Assistant</div>
                <div className="typing-indicator">
                  <span></span>
                  <span></span>
                  <span></span>
                </div>
              </div>
            </div>
          )}
          <div ref={messagesEndRef} />
        </div>

        <form onSubmit={sendMessage} className="input-form">
          <textarea
            value={input}
            onChange={(e) => setInput(e.target.value)}
            onKeyDown={(e) => {
              // Submit on Enter, new line on Shift+Enter
              if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                if (input.trim() && !isLoading) {
                  sendMessage(e);
                }
              }
            }}
            placeholder="Type your message here... (Shift+Enter for new line)"
            disabled={isLoading}
            className="message-input"
            rows="3"
          />
          <button 
            type="submit" 
            disabled={isLoading || !input.trim()}
            className="send-btn"
          >
            {isLoading ? '⏳' : '📤'} Send
          </button>
        </form>
      </div>

      <footer className="app-footer">
        <p>Powered by Spring Boot + React + Ollama (TinyLlama)</p>
      </footer>
    </div>
  );
}

export default App;
