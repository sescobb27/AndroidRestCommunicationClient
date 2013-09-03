class PostSerializer < ActiveModel::Serializer
  attributes :id, :title, :content, :author, :rating
end
