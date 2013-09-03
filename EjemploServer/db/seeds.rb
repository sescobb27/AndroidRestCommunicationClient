# This file should contain all the record creation needed to seed the database with its default values.
# The data can then be loaded with the rake db:seed (or created alongside the db with db:setup).
#
POSTS = [
    {
        title: 'Android',
        content: 'Android Examples',
        author: 'Simon',
        rating: 5
    },
    {
        title: 'Ruby',
        content: 'Ruby Examples',
        author: 'Simon',
        rating: 5
    },
    {
        title: 'Rails',
        content: 'Rails Examples',
        author: 'Simon',
        rating: 5
    },
    {
        title: 'Node',
        content: 'Node Examples',
        author: 'Pepito',
        rating: 2
    }
]
puts "Seeding Data..."
POSTS.each do |post|
    Post.create post
end
puts "Finish Seeding Data"
