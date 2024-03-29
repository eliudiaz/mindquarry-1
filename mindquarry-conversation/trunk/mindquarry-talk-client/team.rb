require 'net/http'
require 'rexml/document'

require 'conversation.rb'

module MindquarryTalk

  class Team
    attr_reader :id, :name, :server
  
    def initialize(server, id)
      @server = server
      @id = id
    end
    
    def getMeta
      @server.http.start do |http|
        request = @server.getRequestForPath "/teams/team/#{id}/"
        response = http.request(request)
        doc = REXML::Document.new(response.body)
        doc.root.each_element do |node|
          if node.name == "name"
            @name = node.text
          end
        end
      end
    end
    
    def getConversations
      conversations = []
      @server.http.start do |http|
        request = @server.getRequestForPath "/talk/#{id}/"
        response = http.request(request)
        doc = REXML::Document.new(response.body)
        doc.root.each_element do |node|
          conversations.push Conversation.new(self, node.attribute('id').value)
        end
      end
      conversations.each do |conv|
        conv.getMeta
      end
      return conversations
    end
    
    def getConversation(id)
      conversation = Conversation.new(self, id)
      conversation.getMeta
      return conversation
    end
    
    def newConversation(title)
      @server.http.start do |http|
        request = @server.putRequestForPath "/talk/#{@id}/new"
        request["content-type"] = "application/xml"
        
        conversation = REXML::Element.new "conversation"
        conversation.add_element("title").add_text(title)
        body = ""
        conversation.write(body)
        
        request.body = body
        response = http.request(request)
        url = response["Location"]
        id = url[0..url.rindex('/')-1]
        Conversation.new(self, id[id.rindex('/')+1..-1], title)
      end
    end
  end
end